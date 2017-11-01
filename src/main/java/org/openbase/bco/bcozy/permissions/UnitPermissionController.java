package org.openbase.bco.bcozy.permissions;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
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
    private TableView<Object> permissionsTable;
    @FXML
    public TableColumn<UnitGroupPermissionViewModel, String> groupColumn;
    @FXML
    public TableColumn<UnitGroupPermissionViewModel, CheckBox> accessColumn;
    @FXML
    public TableColumn<UnitGroupPermissionViewModel, CheckBox> writeColumn;
    @FXML
    public TableColumn<UnitGroupPermissionViewModel, CheckBox> readColumn;

    protected PermissionsService permissionsService = PermissionsServiceImpl.permissionsService;

    private String selectedUnitId;

    private OtherPermissionsViewModel other;

    private List<UnitGroupPermissionViewModel> groupPermissions;


    @FXML
    public void initialize() {
        permissionsTable.setEditable(true);

        groupColumn.prefWidthProperty().bind(permissionsTable.widthProperty().multiply(0.4));
        accessColumn.prefWidthProperty().bind(permissionsTable.widthProperty().multiply(0.2));
        writeColumn.prefWidthProperty().bind(permissionsTable.widthProperty().multiply(0.2));
        readColumn.prefWidthProperty().bind(permissionsTable.widthProperty().multiply(0.2));


        Arrays.asList(accessColumn, readColumn, writeColumn)
                .forEach(column -> column.setComparator((o1, o2) -> Boolean.compare(o1.isSelected(), o2.isSelected())));

    }

    public void setSelectedUnitId(String selectedUnitId) {
        this.selectedUnitId = selectedUnitId;
        try {
            permissionsTable.getItems().clear();
            groupPermissions = permissionsService.getUnitPermissions(selectedUnitId);
            other = permissionsService.getOtherPermissions(selectedUnitId);

            permissionsTable.getItems().add(other);
            permissionsTable.getItems().addAll(groupPermissions);

            ownerPermissionsController.updateOwnerContent(this.selectedUnitId);
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @FXML
    public void save() {
        try {
            OwnerViewModel ownerViewModel = ownerPermissionsController.getSelectedOwner();

            permissionsService.save(selectedUnitId, groupPermissions, ownerViewModel, other);
            //TODO: show Success Message
        } catch (ExecutionException | CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
            //TODO: show Error Message
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
