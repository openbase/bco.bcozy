package org.openbase.bco.bcozy.controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.openbase.bco.bcozy.BCozy;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.mainmenupanes.SettingsPane;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Function;

/**
 * @author vdasilva
 */
public class SettingsController {
    /**
     * Application Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MainMenuController.class);

    @FXML
    public Tab settingsTab;

    @FXML
    private JFXTreeTableView<RecursiveUnitConfig> unitsTable;

    @FXML
    private JFXTextField filterInput;

    @FXML
    private AnchorPane settingsPaneParent;

    private SettingsPane settingsPane;


    /**
     * Default Controller necessary for loading fxml files.
     */
    public SettingsController() {

    }

    @FXML
    public void initialize() {

        fillTreeTableView();

        this.setSettingsPane(new SettingsPane());
    }

    public void fillTreeTableView() {
        unitsTable.setShowRoot(false);
        unitsTable.setEditable(true);

        JFXTreeTableColumn<RecursiveUnitConfig, String> labelColumn = createJFXTreeTableColumn("Units",
                (unit) -> unit.getUnit().getLabel());
        labelColumn.setPrefWidth(150);

        JFXTreeTableColumn<RecursiveUnitConfig, String> descColumn = createJFXTreeTableColumn("Description",
                (unit) -> unit.getUnit().getDescription());
        descColumn.setPrefWidth(150);

        JFXTreeTableColumn<RecursiveUnitConfig, String> typeColumn = createJFXTreeTableColumn("Type",
                (unit) -> unit.getUnit().getType().name());
        typeColumn.setPrefWidth(150);

        unitsTable.getColumns().addAll(typeColumn, labelColumn, descColumn);

        filterInput.textProperty().addListener((o, oldVal, newVal) -> {
            unitsTable.setPredicate(
                    user -> user.getValue().getUnit().getLabel().toLowerCase().contains(newVal.toLowerCase())
                            || user.getValue().getUnit().getDescription().toLowerCase().contains(newVal.toLowerCase())
                            || user.getValue().getUnit().getType().name().toLowerCase().contains(newVal.toLowerCase()));
        });


        try {
            Registries.getUnitRegistry().addDataObserver((observable, unitRegistryData) -> {
                        ObservableList<RecursiveUnitConfig> unitConfigs = FXCollections.observableArrayList();
                        List<UnitConfigType.UnitConfig> unitConfigList = Registries.getUnitRegistry().getUnitConfigs();

                        for (UnitConfigType.UnitConfig unitConfig : unitConfigList) {
                            if (Objects.nonNull(unitConfig)) {
                                unitConfigs.add(new RecursiveUnitConfig(unitConfig));
                            }
                        }

                        if (!unitConfigs.isEmpty()) {
                            RecursiveTreeItem<RecursiveUnitConfig> item = new RecursiveTreeItem<>(
                                    unitConfigs, RecursiveTreeObject::getChildren);
                            unitsTable.setRoot(item);
                        }

                        unitsTable.group(typeColumn);
                    }
            );
        } catch (CouldNotPerformException | InterruptedException e) {
            e.printStackTrace();
        }


    }

    private <S, T> JFXTreeTableColumn<S, T> createJFXTreeTableColumn(String text, Function<S, T> supplier) {
        JFXTreeTableColumn<S, T> column = new JFXTreeTableColumn<>(text);
        column.setCellValueFactory(new MethodRefCellValueFactory<>(supplier, column));
        return column;
    }

    public SettingsPane getSettingsPane() {
        return settingsPane;
    }

    private class RecursiveUnitConfig extends RecursiveTreeObject<RecursiveUnitConfig> {
        final private UnitConfigType.UnitConfig unit;

        public RecursiveUnitConfig(UnitConfigType.UnitConfig unit) {
            this.unit = Objects.requireNonNull(unit);
        }

        public UnitConfigType.UnitConfig getUnit() {
            return unit;
        }
    }

    private class MethodRefCellValueFactory<S, T> implements Callback<TreeTableColumn.CellDataFeatures<S, T>,
            ObservableValue<T>> {

        Function<S, T> supplier;

        JFXTreeTableColumn<S, T> column;

        public MethodRefCellValueFactory(Function<S, T> supplier, JFXTreeTableColumn<S, T>
                column) {
            this.supplier = supplier;
            this.column = column;
        }

        @Override
        public ObservableValue<T> call(TreeTableColumn.CellDataFeatures<S, T> param) {
            if (column.validateValue(param)) {
                return new SimpleObjectProperty(supplier.apply(param.getValue().getValue()));
            }
            return column.getComputedValue(param);
        }
    }

    private void chooseTheme() {
        final ResourceBundle languageBundle = ResourceBundle
                .getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());

        settingsPane.getThemeChoice().getSelectionModel().selectedIndexProperty()
                .addListener(new ChangeListener<Number>() {

                    @Override
                    public void changed(final ObservableValue<? extends Number> observableValue, final Number number,
                                        final Number number2) {
                        if (settingsPane.getAvailableThemes().get(number2.intValue())
                                .equals(languageBundle.getString(Constants.LIGHT_THEME_CSS_NAME))) {
                            BCozy.changeTheme(Constants.LIGHT_THEME_CSS);
                        } else if (settingsPane.getAvailableThemes().get(number2.intValue())
                                .equals(languageBundle.getString(Constants.DARK_THEME_CSS_NAME))) {
                            BCozy.changeTheme(Constants.DARK_THEME_CSS);
                        }
                    }
                });
    }

    private void chooseLanguage() {
        settingsPane.getLanguageChoice().getSelectionModel().selectedIndexProperty()
                .addListener(new ChangeListener<Number>() {

                    @Override
                    public void changed(final ObservableValue<? extends Number> observableValue, final Number number,
                                        final Number number2) {
                        if (settingsPane.getAvailableLanguages().get(number2.intValue()).equals("English")) {
                            LanguageSelection.getInstance().setSelectedLocale(new Locale("en", "US"));
                        } else if (settingsPane.getAvailableLanguages().get(number2.intValue()).equals("Deutsch")) {
                            LanguageSelection.getInstance().setSelectedLocale(new Locale("de", "DE"));
                        }
                    }
                });
    }

    public void setSettingsPane(SettingsPane settingsPane) {
        this.settingsPane = settingsPane;

        this.settingsPane.getThemeChoice().setOnAction(event -> chooseTheme());
        this.settingsPane.getLanguageChoice().setOnAction(event -> chooseLanguage());
        //Necessary to ensure that the first change is not missed by the ChangeListener
        this.settingsPane.getThemeChoice().getSelectionModel().select(0);
        this.settingsPane.getLanguageChoice().getSelectionModel().select(0);

        settingsTab.setContent(this.settingsPane);
    }
}





