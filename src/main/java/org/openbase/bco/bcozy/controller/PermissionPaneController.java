package org.openbase.bco.bcozy.controller;

import com.sun.javafx.font.freetype.HBGlyphLayout;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.openbase.bco.bcozy.view.ObserverLabel;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import rst.domotic.authentication.PermissionConfigType;
import rst.domotic.authentication.PermissionType;
import rst.domotic.unit.UnitConfigType;

import java.util.List;


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
    public Button saveRightsButton;

    @FXML
    public HBox hbox;

    private UnitConfigType.UnitConfig unitConfig;

    private ObservableList<UnitConfigType.UnitConfig> groups = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        this.onWidthChange(null, null, null);
        permissionsTable.widthProperty().addListener(this::onWidthChange);

        usergroupColumn.setGraphic(new ObserverLabel("usergroups"));


        permissionsColumn.setGraphic(new ObserverLabel("permissions"));
        readRights.setGraphic(new ObserverLabel("readRight"));
        writeRights.setGraphic(new ObserverLabel("writeRight"));
        accessRights.setGraphic(new ObserverLabel("accessRight"));


//        usergroupColumn.setCellValueFactory(param ->
//                new SimpleStringProperty(param.getValue().getGroupId())
//        );

        newGroupChoiceBox.setConverter(new StringConverter<UnitConfigType.UnitConfig>() {
            @Override
            public String toString(UnitConfigType.UnitConfig object) {
                return object.getLabel();
            }

            @Override
            public UnitConfigType.UnitConfig fromString(String string) {
                for (UnitConfigType.UnitConfig group : groups) {
                    if ((group.getLabel().equals(string))) {
                        return group;
                    }
                }
                return null;
            }
        });
        newGroupChoiceBox.setItems(groups);
        newGroupChoiceBox.getStyleClass().addAll("bordered-choice-box");
        newGroupChoiceBox.setPrefWidth(-1.0);

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


        try {
            setGroups(Registries.getUserRegistry().getAuthorizationGroupConfigs());
        } catch (CouldNotPerformException | InterruptedException e) {
            // not synchronized yet...
            // e.printStackTrace();
            System.err.println("ICH BIN EIN SOUT, " + e.getMessage());
        }

        try {
            Registries.getUserRegistry().addDataObserver((observable, userRegistryData) -> {
                setGroups(Registries.getUserRegistry().getAuthorizationGroupConfigs());
            });
        } catch (InterruptedException | CouldNotPerformException e) {
            e.printStackTrace();
            System.err.println("ICH BIN EIN ANDERES SOUT, " + e.getMessage());
        }


    }

    private void setGroups(List<UnitConfigType.UnitConfig> groups) {
        System.err.println("PermissionPaneController.initialize.initUser: " + groups.size());
        this.groups.clear();
        this.groups.addAll(groups);
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

        newGroupChoiceBox.setPrefWidth(width/5);
        readRights.setPrefWidth(width/5);
        writeRights.setPrefWidth(width/5);
        accessRights.setPrefWidth(width/5);

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


        PermissionConfigType.PermissionConfig permissionConfig = this.unitConfig.getPermissionConfig()
                .toBuilder()
                .addGroupPermission
                        (mapFieldEntry)
                .build();

        UnitConfigType.UnitConfig unitConfig = this.unitConfig.toBuilder().clearPermissionConfig().mergePermissionConfig
                (permissionConfig)
                .build();


        try {
            Registries.getUnitRegistry().updateUnitConfig(unitConfig);
            this.setUnitConfig(unitConfig);
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
