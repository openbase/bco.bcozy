package org.openbase.bco.bcozy.permissions;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import org.openbase.bco.bcozy.view.ObserverButton;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Controller for editing permissions of one unit.
 *
 * @author vdasilva
 */
public class UnitPermissionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitPermissionController.class);

    @FXML
    public OwnerPermissionsController ownerPermissionsController;
    @FXML
    public Region ownerPermissions;

    @FXML
    private TableView<UnitGroupPermissionViewModel> permissionsTable;
    @FXML
    public TableColumn<UnitGroupPermissionViewModel, String> groupColumn;
    @FXML
    public TableColumn<UnitGroupPermissionViewModel, CheckBox> accessColumn;
    @FXML
    public TableColumn<UnitGroupPermissionViewModel, CheckBox> writeColumn;
    @FXML
    public TableColumn<UnitGroupPermissionViewModel, CheckBox> readColumn;
    @FXML
    public ObserverButton saveRightsButton;
    @FXML
    public Region hbox;

    @FXML
    private CheckBox otherRead;
    @FXML
    private CheckBox otherAccess;
    @FXML
    private CheckBox otherWrite;

    protected PermissionsService permissionsService = PermissionsServiceImpl.permissionsService;

    private String selectedUnitId;

    @FXML
    public void initialize() {

        this.onWidthChange(null, null, null);
        permissionsTable.widthProperty().addListener(this::onWidthChange);
        saveRightsButton.setApplyOnNewText(String::toUpperCase);

        Arrays.asList(accessColumn, readColumn, writeColumn)
                .forEach(column -> column.setComparator((o1, o2) -> Boolean.compare(o1.isSelected(), o2.isSelected())));

        /*
        usergroupColumn.widthProperty().addListener(this::onColumnWidthChange);
//        permissionsColumn.widthProperty().addListener(this::onColumnWidthChange);


*/


/*
        newGroupChoiceBox.setConverter(AuthorizationGroups.stringConverter(groups));
        newGroupChoiceBox.itemsProperty().addListener((observable, oldValue, newValue) -> preselectGroupChoiceBoxValue());
        newGroupChoiceBox.setItems(groups);
        newGroupChoiceBox.setPrefWidth(-1.0);

        newGroupChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectGroup(newValue));

*/
    }

    public void setSelectedUnitId(String selectedUnitId) {
        this.selectedUnitId = selectedUnitId;
        try {
            updatePermissionsContent();
            updateOtherContent();
            ownerPermissionsController.updateOwnerContent(this.selectedUnitId);
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void updateOtherContent() throws CouldNotPerformException, InterruptedException {
        OtherPermissionsViewModel otherPermissionsViewModel = permissionsService.getOtherPermissions(selectedUnitId);

        otherAccess.setSelected(otherPermissionsViewModel.isAccess());
        otherRead.setSelected(otherPermissionsViewModel.isRead());
        otherWrite.setSelected(otherPermissionsViewModel.isWrite());

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

        saveRightsButton.setLayoutX(width - 100.0);
        //TODO: Resize other Elements
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
        //permissionsColumn.setPrefWidth(width - newValue.doubleValue());
        //TODO: Resize other Elements
    }

    private void updatePermissionsContent() throws CouldNotPerformException, InterruptedException {
        List<UnitGroupPermissionViewModel> groupPermissions = permissionsService.getUnitPermissions(selectedUnitId);
        permissionsTable.getItems().setAll(groupPermissions);
    }

    @FXML
    public void save() {
        try {
            OtherPermissionsViewModel other = new OtherPermissionsViewModel(otherRead.isSelected(),
                    otherWrite.isSelected(), otherAccess.isSelected());

            OwnerViewModel ownerViewModel = ownerPermissionsController.getSelectedOwner();

            permissionsService.save(selectedUnitId, permissionsTable.getItems(), ownerViewModel, other);
            //TODO: show Success Message
        } catch (ExecutionException | CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
            //TODO: show Error Message
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
