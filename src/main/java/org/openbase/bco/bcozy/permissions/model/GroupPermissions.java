package org.openbase.bco.bcozy.permissions.model;

/**
 * ViewModel for Permission by a group for an unit, consisting of the group and their rights.
 *
 * @author vdasilva
 */
public class GroupPermissions extends AbstractPermissions {

    private final String groupId;

    public GroupPermissions(String groupId, String groupName, boolean read, boolean write, boolean access) {
        super(groupName, read, write, access);
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

}
