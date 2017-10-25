package org.openbase.bco.bcozy.permissions;

import java.util.logging.Logger;

/**
 * Abstract model for permissions.
 * Hold the Original state of a permission.
 *
 * @author vdasilva
 */
public abstract class AbstractPermissionsViewModel {
    private static final Logger LOG = Logger.getLogger(AbstractPermissionsViewModel.class.getName());

    protected final boolean originalRead, originalWrite, originalAccess;

    public AbstractPermissionsViewModel(boolean read, boolean write, boolean access) {
        this.originalAccess = access;
        this.originalRead = read;
        this.originalWrite = write;
    }

    public abstract boolean changed();

    protected boolean changed(boolean read, boolean write, boolean access) {
        return read != originalRead || access != originalAccess || write != originalWrite;
    }
}
