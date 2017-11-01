package org.openbase.bco.bcozy.permissions;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.logging.Logger;

/**
 * @author vdasilva
 */
public abstract class AbstractTableRowPermissionsViewModel extends AbstractPermissionsViewModel {
    private static final Logger LOG = Logger.getLogger(AbstractTableRowPermissionsViewModel.class.getName());

    private final String name;

    private final BooleanProperty read = new SimpleBooleanProperty();

    private final BooleanProperty write = new SimpleBooleanProperty();

    private final BooleanProperty access = new SimpleBooleanProperty();

    AbstractTableRowPermissionsViewModel(String name, boolean read, boolean write, boolean access) {
        super(read, write, access);
        this.name = name;
        this.read.set(read);
        this.access.set(access);
        this.write.set(write);
    }

    public String getName() {
        return name;
    }

    public boolean isRead() {
        return read.get();
    }

    public BooleanProperty readProperty() {
        return read;
    }

    public void setRead(boolean read) {
        this.read.set(read);
    }

    public boolean isWrite() {
        return write.get();
    }

    public BooleanProperty writeProperty() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write.set(write);
    }

    public boolean isAccess() {
        return access.get();
    }

    public BooleanProperty accessProperty() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access.set(access);
    }

    @Override
    public boolean changed() {
        return changed(isRead(), isWrite(), isAccess());
    }
}
