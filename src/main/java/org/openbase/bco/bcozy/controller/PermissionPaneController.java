package org.openbase.bco.bcozy.controller;

import com.sun.javafx.font.freetype.HBGlyphLayout;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.openbase.bco.bcozy.util.Groups;
import org.openbase.bco.bcozy.view.ObserverButton;
import org.openbase.bco.bcozy.view.ObserverLabel;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import rst.domotic.authentication.PermissionConfigType;
import rst.domotic.authentication.PermissionType;
import rst.domotic.unit.UnitConfigType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * @author vdasilva
 */
public class PermissionPaneController {

    @FXML
    private TableColumn<PermissionConfigType.PermissionConfig.MapFieldEntry, String> usergroupColumn;

    @FXML
    private TableColumn<PermissionConfigType.PermissionConfig.MapFieldEntry, String> permissionsColumn;

    @FXML
    private TableView<PermissionConfigType.PermissionConfig.MapFieldEntry> permissionsTable;

    @FXML
    public ChoiceBox<UnitConfigType.UnitConfig> newGroupChoiceBox;

    @FXML
    public CheckBox readRights;

    @FXML
    public CheckBox writeRights;

    @FXML
    public CheckBox accessRights;

    @FXML
    public ObserverButton saveRightsButton;

    @FXML
    public HBox hbox;

    private UnitConfigType.UnitConfig unitConfig;

    private ObservableList<UnitConfigType.UnitConfig> groups = Groups.getGroups();

    @FXML
    public void initialize() {
        this.onWidthChange(null, null, null);
        permissionsTable.widthProperty().addListener(this::onWidthChange);

        usergroupColumn.setGraphic(new ObserverLabel("usergroups"));


        permissionsColumn.setGraphic(new ObserverLabel("permissions"));
        readRights.setGraphic(new ObserverLabel("readRight"));
        writeRights.setGraphic(new ObserverLabel("writeRight"));
        accessRights.setGraphic(new ObserverLabel("accessRight"));


        saveRightsButton.getStyleClass().clear();
        saveRightsButton.getStyleClass().addAll("transparent-button");
        saveRightsButton.setApplyOnNewText(String::toUpperCase);


        newGroupChoiceBox.setConverter(Groups.stringConverter(groups));
        newGroupChoiceBox.setItems(groups);
        newGroupChoiceBox.setPrefWidth(-1.0);
        preselectGroupChoiceBoxValue();

        groups.addListener((ListChangeListener.Change<? extends UnitConfigType.UnitConfig> c)
                -> preselectGroupChoiceBoxValue()
        );

        usergroupColumn.setCellValueFactory(param -> {
                    for (UnitConfigType.UnitConfig group : groups) {
                        if (param.getValue().getGroupId().equals(group.getId())) {
                            return new SimpleStringProperty(group.getLabel());
                        }
                    }
                    return null;
                }
        );

        permissionsColumn.setCellValueFactory(param ->
                new SimpleStringProperty(
                        (param.getValue().getPermission().getAccess() ? "A" : "a") + ","
                                + (param.getValue().getPermission().getRead() ? "R" : "r") + ","
                                + (param.getValue().getPermission().getWrite() ? "W" : "w") + ","


                )
        );
    }

    private void preselectGroupChoiceBoxValue() {
        if (!groups.isEmpty() && newGroupChoiceBox.getValue() == null) {
            newGroupChoiceBox.setValue(groups.get(0));
        }
    }

    public void updateTableContent() {

        permissionsTable.setItems(FXCollections.observableArrayList());
        for (PermissionConfigType.PermissionConfig.MapFieldEntry group : this.unitConfig.getPermissionConfig()
                .getGroupPermissionList()) {
            permissionsTable.getItems().add(group);
        }


    }

    /**
     * Sets column-widths.
     *
     * @param observable ignored
     * @param oldValue   ignored
     * @param newValue   ignored
     */
    private void onWidthChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        double width = permissionsTable.getWidth();

        hbox.setPrefWidth(width);
        hbox.setSpacing(10.0);

        saveRightsButton.setLayoutX(width - 100.0);

        newGroupChoiceBox.setPrefWidth(width / 5);
        readRights.setPrefWidth(width / 5);
        writeRights.setPrefWidth(width / 5);
        accessRights.setPrefWidth(width / 5);

        usergroupColumn.setPrefWidth(width / 2);
        permissionsColumn.setPrefWidth(width / 2);
    }

    public void setUnitConfig(UnitConfigType.UnitConfig unitConfig) {
        this.unitConfig = unitConfig;
        updateTableContent();
    }

    @FXML
    public void save(final ActionEvent actionEvent) {

        String groupId = "43573651-0c9b-4765-b61b-b20ba31df53e";
        if (newGroupChoiceBox.getSelectionModel() != null && newGroupChoiceBox.getSelectionModel().getSelectedItem()
                != null) {
            //return;
            groupId = newGroupChoiceBox.getSelectionModel().getSelectedItem().getId();
        }


        PermissionConfigType.PermissionConfig.MapFieldEntry mapFieldEntry = PermissionConfigType.PermissionConfig
                .MapFieldEntry.newBuilder()
                .setGroupId(groupId)
                .setPermission(
                        PermissionType.Permission.newBuilder()
                                .setAccess(accessRights.isSelected())
                                .setWrite(writeRights.isSelected())
                                .setRead(readRights.isSelected())
                                .build()
                )
                .build();


        List<PermissionConfigType.PermissionConfig.MapFieldEntry> entryListCopy = new ArrayList<>(this.unitConfig
                .getPermissionConfig().getGroupPermissionList());

        entryListCopy.removeIf(entry -> mapFieldEntry.getGroupId().equals(entry.getGroupId()));

        PermissionConfigType.PermissionConfig permissionConfig = this.unitConfig.getPermissionConfig()
                .toBuilder()
                .clearGroupPermission()
                .addAllGroupPermission(entryListCopy)
                .addGroupPermission(mapFieldEntry)
                .build();

        UnitConfigType.UnitConfig unitConfig = this.unitConfig.toBuilder().clearPermissionConfig()
                .mergePermissionConfig
                        (permissionConfig)
                .build();


        try {
            unitConfig = Registries.getUnitRegistry().updateUnitConfig(unitConfig).get();
            this.setUnitConfig(unitConfig);
        } catch (CouldNotPerformException | ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
