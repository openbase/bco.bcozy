package org.openbase.bco.bcozy.permissions;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.permissions.model.*;
import org.openbase.bco.bcozy.view.InfoPane;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.ExceptionProcessor;
import org.openbase.jul.exception.NotAvailableException;
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
    private TableView<Object> permissionsTable;
    @FXML
    public TableColumn<GroupPermissions, String> groupColumn;
    @FXML
    public TableColumn<GroupPermissions, Boolean> accessColumn;
    @FXML
    public TableColumn<GroupPermissions, Boolean> writeColumn;
    @FXML
    public TableColumn<GroupPermissions, Boolean> readColumn;

    protected PermissionsService permissionsService = PermissionsServiceImpl.INSTANCE;

    private String selectedUnitId;

    private OwnerPermissions ownerPermissions;

    private OtherPermissions other;

    private List<GroupPermissions> groupPermissions;


    @FXML
    public void initialize() {
        permissionsTable.setEditable(true);

        permissionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        groupColumn.setMaxWidth(1f * Integer.MAX_VALUE * 40); // 40% width
        accessColumn.setMaxWidth(1f * Integer.MAX_VALUE * 20); // 20% width
        writeColumn.setMaxWidth(1f * Integer.MAX_VALUE * 20); // 20% width
        readColumn.setMaxWidth(1f * Integer.MAX_VALUE * 20); // 20% width

        Arrays.asList(accessColumn, readColumn, writeColumn)
                .forEach(column -> column.setComparator(Boolean::compare));

    }

    public void setSelectedUnitId(String selectedUnitId) {
        this.selectedUnitId = selectedUnitId;
        try {
            permissionsTable.getItems().clear();

            groupPermissions = permissionsService.getUnitPermissions(selectedUnitId);
            other = permissionsService.getOtherPermissions(selectedUnitId);
            ownerPermissions = permissionsService.getOwner(selectedUnitId);

            permissionsTable.getItems().add(ownerPermissions);
            permissionsTable.getItems().add(other);
            permissionsTable.getItems().addAll(groupPermissions);
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @FXML
    public void save() {
        try {
            permissionsService.save(selectedUnitId, groupPermissions, ownerPermissions, other);
            InfoPane.info("saveSuccess")
                    .backgroundColor(Color.GREEN)
                    .hideAfter(Duration.seconds(5));
        } catch (ExecutionException | CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
            InfoPane.info(LanguageSelection.getLocalized("saveErrorWithMessage", ExceptionProcessor.getInitialCauseMessage(ex)))
                    .backgroundColor(Color.RED)
                    .hideAfter(Duration.seconds(5));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
