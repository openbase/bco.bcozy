package org.openbase.bco.bcozy.permissions;

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

    private boolean read, write, access;


    public OwnerViewModel(String userId, String username, boolean currentOwner) {
        this(userId, username, currentOwner, false, false, false);
    }

    public OwnerViewModel(String userId, String username, boolean currentOwner, boolean read, boolean write, boolean access) {
        super(read, write, access);
        this.userId = Objects.requireNonNull(userId);
        this.username = Objects.requireNonNull(username);
        this.currentOwner = currentOwner;

        this.read = (read);
        this.write = (write);
        this.access = (access);
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
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    @Override
    public boolean changed() {
        return changed(isRead(), isWrite(), isAccess());
    }
}
