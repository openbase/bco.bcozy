package org.openbase.bco.bcozy.permissions.model;

import javafx.beans.property.*;
import rst.language.LabelType.Label;

import java.util.logging.Logger;

/**
 * Abstract model for permissions.
 * Hold the Original state of a permission.
 *
 * @author vdasilva
 */
public abstract class AbstractPermissions {
    private static final Logger LOGGER = Logger.getLogger(AbstractPermissions.class.getName());

    private final boolean originalRead, originalWrite, originalAccess;

    private final ObjectProperty<Label> label = new SimpleObjectProperty<>();

    private final BooleanProperty read = new SimpleBooleanProperty();

    private final BooleanProperty write = new SimpleBooleanProperty();

    private final BooleanProperty access = new SimpleBooleanProperty();

    AbstractPermissions(Label label, boolean read, boolean write, boolean access) {
        this.originalAccess = access;
        this.originalRead = read;
        this.originalWrite = write;

        this.label.setValue(label);
        this.read.set(read);
        this.access.set(access);
        this.write.set(write);
    }

    public Label getLabel() {
        return label.get();
    }

    public ObjectProperty<Label> labelProperty() {
        return label;
    }

    public void setLabel(Label label) {
        this.label.set(label);
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
