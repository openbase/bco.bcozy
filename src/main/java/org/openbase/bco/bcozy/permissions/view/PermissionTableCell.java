package org.openbase.bco.bcozy.permissions.view;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import org.openbase.bco.bcozy.permissions.model.AbstractPermissions;
import org.openbase.bco.bcozy.permissions.model.OwnerPermissions;

/**
 * Table-Cell, which contains a ComboBox for {@link OwnerPermissions}-Rows and simple Text otherwise.
 * <p>
 * Updates the content of {@link OwnerPermissions} according to the selected ComboBox-Value.
 *
 * @author vdasilva
 */
public class PermissionTableCell<T> extends TableCell<AbstractPermissions, T> {

    /**
     * ComboBox for Owner-Selection.
     */
    private final ComboBox<OwnerPermissions.Owner> comboBox = new ComboBox<>();

    /**
     * Current Row, if {@link OwnerPermissions}.
     */
    private OwnerPermissions model;

    PermissionTableCell() {
        this.comboBox.valueProperty().addListener((observable, oldValue, newValue) ->
                {
                    if (model != null) {
                        model.owner = newValue;
                    }
                }
        );
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {

            AbstractPermissions permissionsRow = getTableView().getItems().get(getIndex());

            if (permissionsRow instanceof OwnerPermissions) {
                updateItem((OwnerPermissions) permissionsRow);
            } else {
                setGraphic(null);
                setText(permissionsRow.getName());
            }
        }
    }

    private void updateItem(OwnerPermissions ownerPermissions) {
        this.model = ownerPermissions;

        comboBox.getItems().setAll(this.model.owners);
        comboBox.getSelectionModel().select(this.model.owner);

        setText(null);
        setGraphic(comboBox);
    }
}
