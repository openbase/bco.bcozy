package org.openbase.bco.bcozy.permissions.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.logging.Logger;

/**
 * Abstract model for permissions.
 * Hold the Original state of a permission.
 *
 * @author vdasilva
 */
public abstract class AbstractPermissions {
    private static final Logger LOG = Logger.getLogger(AbstractPermissions.class.getName());

    private final boolean originalRead, originalWrite, originalAccess;

    private final StringProperty name = new SimpleStringProperty();

    private final BooleanProperty read = new SimpleBooleanProperty();

    private final BooleanProperty write = new SimpleBooleanProperty();

    private final BooleanProperty access = new SimpleBooleanProperty();

    AbstractPermissions(String name, boolean read, boolean write, boolean access) {
        this.originalAccess = access;
        this.originalRead = read;
        this.originalWrite = write;

        this.name.setValue(name);
        this.read.set(read);
        this.access.set(access);
        this.write.set(write);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
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

    public boolean changed() {
        return changed(isRead(), isWrite(), isAccess());
    }

    protected boolean changed(boolean read, boolean write, boolean access) {
        return read != originalRead || access != originalAccess || write != originalWrite;
    }
}
