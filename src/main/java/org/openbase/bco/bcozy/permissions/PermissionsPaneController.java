package org.openbase.bco.bcozy.permissions;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import org.controlsfx.control.textfield.CustomTextField;
import org.openbase.bco.bcozy.permissions.model.RecursiveUnitConfig;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ObserverLabel;
import org.openbase.jul.visual.javafx.JFXConstants;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.util.Objects.nonNull;

/**
 * Controller for selecting unit to edit permissions for.
 * Permissions of the selected group are edited wih subcontroller {@link UnitPermissionController}.
 *
 * @author vdasilva
 */
public class PermissionsPaneController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionsPaneController.class);

    @FXML
    private Parent unitPermission;

    @FXML
    private UnitPermissionController unitPermissionController;

    @FXML
    private CustomTextField filterInput;

    @FXML
    private JFXTreeTableView<RecursiveUnitConfig> unitsTable;
    @FXML
    private JFXTreeTableColumn<RecursiveUnitConfig, String> typeColumn;
    @FXML
    private JFXTreeTableColumn<RecursiveUnitConfig, String> descColumn;
    @FXML
    private JFXTreeTableColumn<RecursiveUnitConfig, String> labelColumn;

    private final ObservableList<RecursiveUnitConfig> list = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        fillTreeTableView();

        try {
            Registries.getUnitRegistry().addDataObserver((observable, unitRegistryData) -> fillTable());
            fillTable();
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

    }

    private void onSelectionChange(javafx.beans.Observable observable, TreeItem oldValue, TreeItem newValue) {
        if (nonNull(newValue) && newValue.getValue() instanceof RecursiveUnitConfig) {
            setUnitPermissionVisible(true);
            unitPermissionController.setSelectedUnitId(((RecursiveUnitConfig) newValue.getValue()).getUnit().getId());
        } else {
            setUnitPermissionVisible(false);
        }
    }

    private void setUnitPermissionVisible(boolean visible) {
        unitPermission.setVisible(visible);
    }

    private void fillTreeTableView() {
        labelColumn.setCellValueFactory(new MethodRefCellValueFactory<>((unit) -> unit.getUnit().getLabel(), labelColumn));

        descColumn.setCellValueFactory(new MethodRefCellValueFactory<>((unit) -> unit.getUnit().getDescription(), descColumn));

        typeColumn.setCellValueFactory(new MethodRefCellValueFactory<>((unit) -> unit.getUnit().getUnitType().name(), typeColumn));

        RecursiveTreeItem<RecursiveUnitConfig> item = new RecursiveTreeItem<>(list, RecursiveTreeObject::getChildren);
        unitsTable.setRoot(item);

        unitsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener(this::onSelectionChange);

        filterInput.setRight(new SVGGlyphIcon(FontAwesomeIcon.SEARCH, JFXConstants.ICON_SIZE_EXTRA_SMALL, true));

        filterInput.promptTextProperty().setValue(new ObserverLabel("searchPlaceholder").getText());
        filterInput.textProperty().addListener((o, oldVal, newVal) -> {
            unitsTable.setPredicate(
                    user -> user.getValue().getUnit().getLabel().toLowerCase().contains(newVal.toLowerCase())
                    || user.getValue().getUnit().getDescription().toLowerCase().contains(newVal.toLowerCase())
                    || user.getValue().getUnit().getUnitType().name().toLowerCase().contains(newVal.toLowerCase()));
        });

    }

    private class MethodRefCellValueFactory<S, T> implements Callback<TreeTableColumn.CellDataFeatures<S, T>, ObservableValue<T>> {

        Function<S, T> supplier;

        JFXTreeTableColumn<S, T> column;

        public MethodRefCellValueFactory(Function<S, T> supplier, JFXTreeTableColumn<S, T> column) {
            this.supplier = Objects.requireNonNull(supplier);
            this.column = Objects.requireNonNull(column);
        }

        @Override
        public ObservableValue<T> call(TreeTableColumn.CellDataFeatures<S, T> param) {
            if (column.validateValue(param)) {
                return new SimpleObjectProperty<>(supplier.apply(param.getValue().getValue()));
            }
            return column.getComputedValue(param);
        }
    }

    private void fillTable() throws CouldNotPerformException, InterruptedException {
        if (Registries.getUnitRegistry().isDataAvailable()) {
            List<UnitConfigType.UnitConfig> unitConfigList = Registries.getUnitRegistry().getUnitConfigs();
            Platform.runLater(() -> fillTable(unitConfigList));
        }
    }

    private void fillTable(List<UnitConfigType.UnitConfig> unitConfigList) {

        unitsTable.unGroup(this.typeColumn);

        list.clear();

        for (UnitConfigType.UnitConfig unitConfig : unitConfigList) {
            if (nonNull(unitConfig)) {
                list.add(new RecursiveUnitConfig(unitConfig));
            }
        }

        if (!list.isEmpty()) {
            unitsTable.group(this.typeColumn);
        }
    }

}
