package org.openbase.bco.bcozy.permissions.model;

import org.openbase.jul.exception.CouldNotPerformException;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author vdasilva
 */
public interface PermissionsService {

    /**
     * Returns a List of all possible Owners of an unit.
     * The current owner is marked with {@link OwnerPermissions#currentOwner}.
     *
     * @param selectedUnitId the unit to get all possible owners
     * @return List of all possible Owners
     */
    OwnerPermissions getOwner(String selectedUnitId) throws CouldNotPerformException, InterruptedException;

    /**
     * Returns a list of all group permissions, no matter if the group currently have any rights.
     *
     * @param selectedUnitId the unit to get all Group-permissions
     * @return List of all group permissions
     */
    List<GroupPermissions> getUnitPermissions(String selectedUnitId) throws CouldNotPerformException, InterruptedException;

    /**
     * Saves the new Settings for the unit.
     * <p>
     * Permissions for Group and the Owner and his permissions are set.
     *  @param selectedUnitId the unit to save permissions for
     * @param permissions    the group-permissions to save
     * @param owner          the new Owner (or {@link OwnerPermissions#currentOwner} if none
     * @param other
     */
    void save(String selectedUnitId, List<GroupPermissions> permissions, OwnerPermissions owner, OtherPermissions other) throws CouldNotPerformException, InterruptedException, ExecutionException;

    OtherPermissions getOtherPermissions(String selectedUnitId) throws CouldNotPerformException, InterruptedException;
}
