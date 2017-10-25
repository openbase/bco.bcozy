package org.openbase.bco.bcozy.permissions;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.util.AuthorizationGroups;
import org.openbase.bco.bcozy.view.ObserverButton;
import org.openbase.bco.bcozy.view.ObserverLabel;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.authentication.PermissionConfigType;
import rst.domotic.authentication.PermissionType;
import rst.domotic.unit.UnitConfigType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


/**
 * @author vdasilva
 */
public class UnitPermissionController {


    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UnitPermissionController.class);

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

    private ObservableList<UnitConfigType.UnitConfig> groups = AuthorizationGroups.getAuthorizationGroups();

    @FXML
    public void initialize() {
        this.onWidthChange(null, null, null);
        permissionsTable.widthProperty().addListener(this::onWidthChange);

        usergroupColumn.setGraphic(new ObserverLabel("usergroups"));
        permissionsColumn.setGraphic(new ObserverLabel("permissions"));
        usergroupColumn.widthProperty().addListener(this::onColumnWidthChange);
//        permissionsColumn.widthProperty().addListener(this::onColumnWidthChange);

        readRights.setGraphic(new ObserverLabel("readRight"));
        writeRights.setGraphic(new ObserverLabel("writeRight"));
        accessRights.setGraphic(new ObserverLabel("accessRight"));


        saveRightsButton.getStyleClass().clear();
        saveRightsButton.getStyleClass().addAll("transparent-button");
        saveRightsButton.setApplyOnNewText(String::toUpperCase);


        newGroupChoiceBox.setConverter(AuthorizationGroups.stringConverter(groups));
        newGroupChoiceBox.itemsProperty().addListener((observable, oldValue, newValue) -> preselectGroupChoiceBoxValue());
        newGroupChoiceBox.setItems(groups);
        newGroupChoiceBox.setPrefWidth(-1.0);

        usergroupColumn.setCellValueFactory(param -> groupLabel(param.getValue().getGroupId()));

        permissionsColumn.setCellValueFactory(param -> formatPermissions(param.getValue().getPermission()));

        permissionsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectedTableEntry(newValue));
        newGroupChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectGroup(newValue));
    }

    private void selectedTableEntry(PermissionConfigType.PermissionConfig.MapFieldEntry entry) {
        resetFields();

        if (permissionsTable.getSelectionModel().getSelectedItem() != entry) {
            permissionsTable.getSelectionModel().select(entry);
        }
        if (entry != null) {
            Optional<UnitConfigType.UnitConfig> group = group(entry.getGroupId());

            if (!group.isPresent()) {
                return;
            }
            if (newGroupChoiceBox.getSelectionModel().getSelectedItem() != group.get()) {
                newGroupChoiceBox.getSelectionModel().select(group.get());
            }
            setRights(entry.getPermission().getRead(),
                    entry.getPermission().getWrite(),
                    entry.getPermission().getAccess());
        }

    }

    private void selectGroup(UnitConfigType.UnitConfig group) {
        selectedTableEntry(permissionEntryForGroup(group.getId()));
    }

    private PermissionConfigType.PermissionConfig.MapFieldEntry permissionEntryForGroup(String groupId) {
        for (PermissionConfigType.PermissionConfig.MapFieldEntry entry : this.unitConfig.getPermissionConfig().getGroupPermissionList()) {
            if (entry.getGroupId().equals(groupId)) {
                return entry;
            }
        }
        return null;
    }

    private Optional<UnitConfigType.UnitConfig> group(String groupId) {
        for (UnitConfigType.UnitConfig group : groups) {
            if (groupId.equals(group.getId())) {
                return Optional.of(group);
            }
        }
        return Optional.empty();
    }

    private SimpleStringProperty groupLabel(String groupId) {
        return group(groupId).map(UnitConfigType.UnitConfig::getLabel)
                .map(SimpleStringProperty::new)
                .orElse(null);
    }

    private SimpleStringProperty formatPermissions(PermissionType.Permission permission) {

        List<String> rights = new ArrayList<>();
       if (permission.getRead()) {
           rights.add(LanguageSelection.getLocalized("readRight"));
       }
       if (permission.getWrite()) {
           rights.add(LanguageSelection.getLocalized("writeRight"));
       }
        if (permission.getAccess()) {
            rights.add(LanguageSelection.getLocalized("accessRight"));
        }

        return new SimpleStringProperty(String.join(",", rights));
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

    /**
     * Dynamically adjust column widths to fill whole space once the width of one column was changed.
     *
     * @param observable ignored
     * @param oldValue   ignored
     * @param newValue   ignored
     */
    private void onColumnWidthChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        double width = permissionsTable.getWidth();
        permissionsColumn.setPrefWidth(width - newValue.doubleValue());
    }

    public void setUnitConfig(UnitConfigType.UnitConfig unitConfig) {
        this.unitConfig = unitConfig;
        updateTableContent();
        selectedTableEntry(null);
    }

    @FXML
    public void save() {
        if (newGroupChoiceBox.getSelectionModel().isEmpty()) {
            return;
        }

        final String groupId = newGroupChoiceBox.getSelectionModel().getSelectedItem().getId();

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
        } catch (CouldNotPerformException | ExecutionException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void resetFields() {
        setRights(false, false, false);
    }

    private void setRights(boolean read, boolean write, boolean access) {
        readRights.setSelected(read);
        writeRights.setSelected(write);
        accessRights.setSelected(access);
    }
}
