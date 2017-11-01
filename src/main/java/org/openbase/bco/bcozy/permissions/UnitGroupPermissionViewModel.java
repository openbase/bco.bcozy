package org.openbase.bco.bcozy.permissions;

/**
 * ViewModel for Permission by a group for an unit, consisting of the group and their rights.
 *
 * @author vdasilva
 */
public class UnitGroupPermissionViewModel extends AbstractTableRowPermissionsViewModel {

    private final String groupId;

    public UnitGroupPermissionViewModel(String groupId, String groupName, boolean read, boolean write, boolean access) {
        super(groupName, read, write, access);
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

}
