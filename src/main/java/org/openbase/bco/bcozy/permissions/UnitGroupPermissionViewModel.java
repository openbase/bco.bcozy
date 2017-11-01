package org.openbase.bco.bcozy.permissions;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * ViewModel for Permission by a group for an unit, consisting of the group and their rights.
 *
 * @author vdasilva
 */
public class UnitGroupPermissionViewModel extends AbstractPermissionsViewModel {

    private final String groupId;

    private final String groupName;

    private final BooleanProperty read = new SimpleBooleanProperty();

    private final BooleanProperty write = new SimpleBooleanProperty();

    private final BooleanProperty access = new SimpleBooleanProperty();

    public UnitGroupPermissionViewModel(String groupId, String groupName, boolean read, boolean write, boolean access) {
        super(read, write, access);
        this.groupId = groupId;
        this.groupName = groupName;

        this.read.set(read);
        this.access.set(access);
        this.write.set(write);
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }


    public boolean isRead() {
        return read.get();
    }

    public BooleanProperty readProperty() {
        return read;
    }

    public boolean isWrite() {
        return write.get();
    }

    public BooleanProperty writeProperty() {
        return write;
    }

    public boolean isAccess() {
        return access.get();
    }

    public BooleanProperty accessProperty() {
        return access;
    }

    @Override
    public boolean changed() {
        return changed(isRead(), isWrite(), isAccess());
    }
}
