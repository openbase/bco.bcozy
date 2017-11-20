package org.openbase.bco.bcozy.permissions.model;

import javafx.collections.ObservableList;
import org.openbase.bco.bcozy.permissions.UnitPermissionController;
import org.openbase.bco.bcozy.util.AuthorizationGroups;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.authentication.PermissionConfigType;
import rst.domotic.authentication.PermissionType;
import rst.domotic.unit.UnitConfigType;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static rst.domotic.authentication.PermissionConfigType.PermissionConfig;
import static rst.domotic.unit.UnitConfigType.UnitConfig;

/**
 * @author vdasilva
 */
public final class PermissionsServiceImpl implements PermissionsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnitPermissionController.class);

    public static final PermissionsServiceImpl INSTANCE = new PermissionsServiceImpl();

    private final ObservableList<UnitConfigType.UnitConfig> groups = AuthorizationGroups.getAuthorizationGroups();

    /**
     * Preloaded Unit list, 'cause Registries.getUnitRegistry().getUnitConfigById(id) needs > 5sek.
     */
    private final List<UnitConfigType.UnitConfig> cachedUnits = new ArrayList<>();

    public PermissionsServiceImpl() {
        fillCache();
    }


    private void fillCache() {
        try {
            if (Registries.getUnitRegistry().isDataAvailable()) {
                setCachedUnits(Registries.getUnitRegistry().getUnitConfigs());
            }
            Registries.getUnitRegistry().addDataObserver((source, data) -> setCachedUnits(Registries.getUnitRegistry().getUnitConfigs()));
        } catch (InterruptedException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
            Thread.currentThread().interrupt();
        }
    }

    private void setCachedUnits(List<UnitConfigType.UnitConfig> units) {
        cachedUnits.clear();
        cachedUnits.addAll(units);
    }

    @Override
    public OwnerPermissions getOwner(String selectedUnitId) throws CouldNotPerformException, InterruptedException {
        if (selectedUnitId == null) {
            return null;
        }

        UnitConfig unit = findUnitConfigById(selectedUnitId);

        String currentOwnerId = unit.getPermissionConfig().getOwnerId();

        List<UnitConfigType.UnitConfig> users = Registries.getUserRegistry().getUserConfigs();


        OwnerPermissions.Owner currentOwner = OwnerPermissions.NULL_OWNER;
        List<OwnerPermissions.Owner> possibleOwners = new ArrayList<>();
        possibleOwners.add(OwnerPermissions.NULL_OWNER);
        for (UnitConfig user : users) {

            final boolean isCurrentOwner = Objects.equals(user.getId(), currentOwnerId);
            final OwnerPermissions.Owner model = new OwnerPermissions.Owner(user.getId(), user.getUserConfig().getUserName());
            if (isCurrentOwner) {
                currentOwner = model;
            }
            possibleOwners.add(model);
        }

        OwnerPermissions ownerPermissions = new OwnerPermissions(currentOwner, possibleOwners,
                unit.getPermissionConfig().getOwnerPermission().getRead(),
                unit.getPermissionConfig().getOwnerPermission().getWrite(),
                unit.getPermissionConfig().getOwnerPermission().getAccess()
        );


        return ownerPermissions;
    }

    @Override
    public List<GroupPermissions> getUnitPermissions(String selectedUnitId) throws CouldNotPerformException, InterruptedException {
        if (selectedUnitId == null) {
            return Collections.emptyList();
        }

        UnitConfig unit = findUnitConfigById(selectedUnitId);

        List<GroupPermissions> groupPermissions = new ArrayList<>();

        for (UnitConfig group : groups) {
            Optional<PermissionType.Permission> permission = permissionEntryForGroup(unit, group.getId())
                    .map(PermissionConfig.MapFieldEntry::getPermission);

            boolean read = permission.map(PermissionType.Permission::getRead).orElse(false);
            boolean write = permission.map(PermissionType.Permission::getWrite).orElse(false);
            boolean access = permission.map(PermissionType.Permission::getAccess).orElse(false);

            GroupPermissions model = new GroupPermissions(group.getId(), group.getLabel(), read, write, access);
            groupPermissions.add(model);
        }

        return groupPermissions;
    }

    @Override
    public OtherPermissions getOtherPermissions(String selectedUnitId) throws CouldNotPerformException, InterruptedException {
        if (selectedUnitId == null) {
            return null;
        }

        UnitConfig unitConfig = findUnitConfigById(selectedUnitId);

        return new OtherPermissions(unitConfig.getPermissionConfig().getOtherPermission().getRead(),
                unitConfig.getPermissionConfig().getOtherPermission().getWrite(),
                unitConfig.getPermissionConfig().getOtherPermission().getAccess());
    }

    @Override
    public void save(@Nonnull String selectedUnitId, List<GroupPermissions> permissions, @Nonnull OwnerPermissions owner, OtherPermissions other) throws CouldNotPerformException, InterruptedException, ExecutionException {
        boolean changed = false;

        UnitConfig unitConfig = findUnitConfigById(selectedUnitId);

        if (owner.changed()) {
            changed = true;
        }

        if (other.changed()) {
            changed = true;
        }

        List<PermissionConfigType.PermissionConfig.MapFieldEntry> permissionEntries = new ArrayList<>(unitConfig.getPermissionConfig().getGroupPermissionList());

        for (GroupPermissions permission : permissions) {
            if (permission.changed()) {
                permissionEntries.removeIf(entry -> permission.getGroupId().equals(entry.getGroupId()));
                permissionEntries.add(toEntry(permission));
                changed = true;
            }
        }

        if (changed) {
            save(unitConfig, owner, permissionEntries, other);
        }
    }

    private void save(UnitConfig unitConfig, OwnerPermissions ownerPermissions, List<PermissionConfig.MapFieldEntry> permissionEntries, OtherPermissions other)
            throws CouldNotPerformException, InterruptedException, ExecutionException {
        PermissionConfig.Builder permissionConfigBuilder = unitConfig.getPermissionConfig()
                .toBuilder()
                .clearGroupPermission()
                .addAllGroupPermission(permissionEntries)
                .clearOwnerId()
                .clearOtherPermission()
                .setOtherPermission(PermissionType.Permission.newBuilder()
                        .setAccess(other.isAccess())
                        .setWrite(other.isWrite())
                        .setRead(other.isRead()))
                .clearOwnerPermission();

        if (ownerPermissions.owner != OwnerPermissions.NULL_OWNER) {
            permissionConfigBuilder = permissionConfigBuilder.setOwnerId(ownerPermissions.owner.getUserId())
                    .setOwnerPermission(PermissionType.Permission.newBuilder()
                            .setAccess(ownerPermissions.isAccess())
                            .setWrite(ownerPermissions.isWrite())
                            .setRead(ownerPermissions.isRead()));
        }


        final UnitConfig newUnitConfig = unitConfig.toBuilder().clearPermissionConfig()
                .mergePermissionConfig(permissionConfigBuilder.build())
                .build();


        final UnitConfig saved = Registries.getUnitRegistry().updateUnitConfig(newUnitConfig).get();

    }
    
    private PermissionConfig.MapFieldEntry toEntry(GroupPermissions groupPermissions) {
        return PermissionConfig
                .MapFieldEntry.newBuilder()
                .setGroupId(groupPermissions.getGroupId())
                .setPermission(PermissionType.Permission.newBuilder()
                        .setAccess(groupPermissions.isAccess())
                        .setWrite(groupPermissions.isWrite())
                        .setRead(groupPermissions.isRead())
                        .build())
                .build();
    }

    private UnitConfig findUnitConfigById(String id) throws CouldNotPerformException, InterruptedException {
        //very slow...
        //UnitConfigType.UnitConfig unit = Registries.getUnitRegistry().getUnitConfigById(id);

        UnitConfig unit = cachedUnits.stream().filter(unitConfig -> unitConfig.getId().equals(id)).findAny().orElse(null);

        return unit;
    }

    private Optional<PermissionConfig.MapFieldEntry> permissionEntryForGroup(UnitConfig unit, String groupId) {
        for (PermissionConfig.MapFieldEntry entry : unit.getPermissionConfig().getGroupPermissionList()) {
            if (entry.getGroupId().equals(groupId)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

}
