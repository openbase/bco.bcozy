package org.openbase.bco.bcozy.permissions.view;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.openbase.bco.bcozy.permissions.model.AbstractPermissions;

/**
 * @author vdasilva
 */
public class PermissionTableCellFactory<T> implements Callback<TableColumn<AbstractPermissions, T>, TableCell<AbstractPermissions, T>> {
    public TableCell<AbstractPermissions, T> call(TableColumn<AbstractPermissions, T> param) {
        return new PermissionTableCell<T>();
    }
}
