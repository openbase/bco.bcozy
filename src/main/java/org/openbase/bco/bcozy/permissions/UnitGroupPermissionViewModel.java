package org.openbase.bco.bcozy.permissions;

import javafx.scene.control.CheckBox;

/**
 * ViewModel for Permission by a group for an unit, consisting of the group and their rights.
 *
 * @author vdasilva
 */
public class UnitGroupPermissionViewModel extends AbstractPermissionsViewModel {

    private final String groupId;

    private final String groupName;

    private final CheckBox read = new CheckBox();

    private final CheckBox write = new CheckBox();

    private final CheckBox access = new CheckBox();

    public UnitGroupPermissionViewModel(String groupId, String groupName, boolean read, boolean write, boolean access) {
        super(read, write, access);
        this.groupId = groupId;
        this.groupName = groupName;

        this.read.setSelected(read);
        this.access.setSelected(access);
        this.write.setSelected(write);
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }


    public CheckBox getRead() {
        return read;
    }

    public CheckBox getWrite() {
        return write;
    }

    public CheckBox getAccess() {
        return access;
    }


    public boolean isRead() {
        return read.isSelected();
    }

    public boolean isWrite() {
        return write.isSelected();
    }

    public boolean isAccess() {
        return access.isSelected();
    }

    @Override
    public boolean changed() {
        return changed(isRead(), isWrite(), isAccess());
    }
}
