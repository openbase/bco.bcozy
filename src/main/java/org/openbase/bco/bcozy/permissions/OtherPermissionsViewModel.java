package org.openbase.bco.bcozy.permissions;

import java.util.logging.Logger;

/**
 * @author vdasilva
 */
public final class OtherPermissionsViewModel extends AbstractPermissionsViewModel {
    private static final Logger LOG = Logger.getLogger(OtherPermissionsViewModel.class.getName());

    private boolean read, write, access;

    public OtherPermissionsViewModel(boolean read, boolean write, boolean access) {
        super(read, write, access);

        this.read = (read);
        this.write = (write);
        this.access = (access);
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
