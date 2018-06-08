package org.openbase.bco.bcozy.permissions.model;

import rst.configuration.LabelType.Label;

/**
 * ViewModel for Permission by a group for an unit, consisting of the group and their rights.
 *
 * @author vdasilva
 */
public class GroupPermissions extends AbstractPermissions {

    private final String groupId;

    public GroupPermissions(String groupId, Label label, boolean read, boolean write, boolean access) {
        super(label, read, write, access);
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

}
