package org.openbase.bco.bcozy.permissions;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Objects;

/**
 * ViewModel for Owner of any Unit.
 *
 * @author vdasilva
 */
public class OwnerViewModel extends AbstractPermissionsViewModel {

    /**
     * Represents no owner.
     */
    final static OwnerViewModel NULL_OBJECT = new OwnerViewModel("", "", false);

    private final String userId;

    private final String username;

    private final boolean currentOwner;

    private BooleanProperty read, write, access;


    public OwnerViewModel(String userId, String username, boolean currentOwner) {
        this(userId, username, currentOwner, false, false, false);
    }

    public OwnerViewModel(String userId, String username, boolean currentOwner, boolean read, boolean write, boolean access) {
        super(read, write, access);
        this.userId = Objects.requireNonNull(userId);
        this.username = Objects.requireNonNull(username);
        this.currentOwner = currentOwner;

        this.read = new SimpleBooleanProperty(read);
        this.write = new SimpleBooleanProperty(write);
        this.access = new SimpleBooleanProperty(access);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OwnerViewModel that = (OwnerViewModel) o;

        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    @Override
    public String toString() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public boolean isCurrentOwner() {
        return currentOwner;
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
