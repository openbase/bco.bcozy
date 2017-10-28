package org.openbase.bco.bcozy.permissions;

import javafx.collections.ObservableList;
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

    protected static final PermissionsServiceImpl permissionsService = new PermissionsServiceImpl();

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
    public List<OwnerViewModel> getOwners(String selectedUnitId) throws CouldNotPerformException, InterruptedException {
        if (selectedUnitId == null) {
            return Collections.emptyList();
        }

        UnitConfig unit = findUnitConfigById(selectedUnitId);

        String currentOwnerId = unit.getPermissionConfig().getOwnerId();

        List<UnitConfigType.UnitConfig> users = Registries.getUserRegistry().getUserConfigs();

        List<OwnerViewModel> ownerModels = new ArrayList<>();
        for (UnitConfig user : users) {
            final boolean isCurrentOwner = Objects.equals(user.getId(), currentOwnerId);
            final OwnerViewModel model;
            if (isCurrentOwner) {
                model = new OwnerViewModel(user.getId(), user.getUserConfig().getUserName(), isCurrentOwner,
                        unit.getPermissionConfig().getOwnerPermission().getRead(),
                        unit.getPermissionConfig().getOwnerPermission().getWrite(),
                        unit.getPermissionConfig().getOwnerPermission().getAccess()
                );
            } else {
                model = new OwnerViewModel(user.getId(), user.getUserConfig().getUserName(), isCurrentOwner);
            }

            ownerModels.add(model);
        }

        return ownerModels;
    }

    @Override
    public List<UnitGroupPermissionViewModel> getUnitPermissions(String selectedUnitId) throws CouldNotPerformException, InterruptedException {
        if (selectedUnitId == null) {
            return Collections.emptyList();
        }

        UnitConfig unit = findUnitConfigById(selectedUnitId);

        List<UnitGroupPermissionViewModel> groupPermissions = new ArrayList<>();

        for (UnitConfig group : groups) {
            Optional<PermissionType.Permission> permission = permissionEntryForGroup(unit, group.getId())
                    .map(PermissionConfig.MapFieldEntry::getPermission);

            boolean read = permission.map(PermissionType.Permission::getRead).orElse(false);
            boolean write = permission.map(PermissionType.Permission::getWrite).orElse(false);
            boolean access = permission.map(PermissionType.Permission::getAccess).orElse(false);

            UnitGroupPermissionViewModel model = new UnitGroupPermissionViewModel(group.getId(), group.getLabel(), read, write, access);
            groupPermissions.add(model);
        }

        return groupPermissions;
    }

    @Override
    public OtherPermissionsViewModel getOtherPermissions(String selectedUnitId) throws CouldNotPerformException, InterruptedException {
        if (selectedUnitId == null) {
            return null;
        }

        UnitConfig unitConfig = findUnitConfigById(selectedUnitId);

        return new OtherPermissionsViewModel(unitConfig.getPermissionConfig().getOtherPermission().getRead(),
                unitConfig.getPermissionConfig().getOtherPermission().getWrite(),
                unitConfig.getPermissionConfig().getOtherPermission().getAccess());
    }

    @Override
    public void save(@Nonnull String selectedUnitId, List<UnitGroupPermissionViewModel> permissions, @Nonnull OwnerViewModel owner, OtherPermissionsViewModel other) throws CouldNotPerformException, InterruptedException, ExecutionException {
        boolean changed = false;

        UnitConfig unitConfig = findUnitConfigById(selectedUnitId);

        if (!owner.isCurrentOwner() || owner.changed()) {
            changed = true;
        }

        if (other.changed()) {
            changed = true;
        }

        List<PermissionConfigType.PermissionConfig.MapFieldEntry> permissionEntries = new ArrayList<>(unitConfig.getPermissionConfig().getGroupPermissionList());

        for (UnitGroupPermissionViewModel permission : permissions) {
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

    private PermissionConfig.MapFieldEntry toEntry(UnitGroupPermissionViewModel unitGroupPermissionViewModel) {
        return PermissionConfig
                .MapFieldEntry.newBuilder()
                .setGroupId(unitGroupPermissionViewModel.getGroupId())
                .setPermission(PermissionType.Permission.newBuilder()
                        .setAccess(unitGroupPermissionViewModel.isAccess())
                        .setWrite(unitGroupPermissionViewModel.isWrite())
                        .setRead(unitGroupPermissionViewModel.isRead())
                        .build())
                .build();
    }

    private void save(UnitConfig unitConfig, OwnerViewModel owner, List<PermissionConfig.MapFieldEntry> permissionEntries, OtherPermissionsViewModel other)
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

        if (owner != OwnerViewModel.NULL_OBJECT) {
            permissionConfigBuilder = permissionConfigBuilder.setOwnerId(owner.getUserId())
                    .setOwnerPermission(PermissionType.Permission.newBuilder()
                            .setAccess(owner.isAccess())
                            .setWrite(owner.isWrite())
                            .setRead(owner.isRead()));
        }


        final UnitConfig newUnitConfig = unitConfig.toBuilder().clearPermissionConfig()
                .mergePermissionConfig(permissionConfigBuilder.build())
                .build();


        final UnitConfig saved = Registries.getUnitRegistry().updateUnitConfig(newUnitConfig).get();

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
