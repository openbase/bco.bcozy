package org.openbase.bco.bcozy.permissions;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import org.openbase.bco.bcozy.permissions.model.AbstractPermissions;
import org.openbase.bco.bcozy.permissions.model.OwnerPermissions;

/**
 * @author vdasilva
 */
public class PermissionTableCell<T> extends TableCell<AbstractPermissions, T> {

    ComboBox<OwnerPermissions.Owner> comboBox = new ComboBox<>();
    OwnerPermissions model;

    public PermissionTableCell() {
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

            AbstractPermissions model = getTableView().getItems().get(getIndex());

            if (model instanceof OwnerPermissions) {
                this.model = (OwnerPermissions) model;

                comboBox.getItems().setAll(this.model.owners);
                comboBox.getSelectionModel().select(this.model.owner);

                setText(null);
                setGraphic(comboBox);
            } else {
                setGraphic(null);
                setText(model.getName());
            }
        }
    }
}
