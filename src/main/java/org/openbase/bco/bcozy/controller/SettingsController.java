package org.openbase.bco.bcozy.controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.openbase.bco.bcozy.BCozy;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ObserverLabel;
import org.openbase.bco.bcozy.view.mainmenupanes.SettingsPane;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Function;

import static java.util.Objects.nonNull;

/**
 * @author vdasilva
 */
public class SettingsController {
    /**
     * Application Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MainMenuController.class);

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab settingsTab;

    @FXML
    private Tab permissionTab;

    @FXML
    private Tab registrationTab;

    @FXML
    private JFXTreeTableView<RecursiveUnitConfig> unitsTable;

    @FXML
    private JFXTextField filterInput;


    @FXML
    private VBox permissionPaneParent;

    private Pane permissionPane;

    private RegistrationController registrationController;

    private SettingsPane settingsPane;
    private PermissionPaneController permissionPaneController;
    private JFXTreeTableColumn<RecursiveUnitConfig, String> typeColumn;


    final ObservableList<RecursiveUnitConfig> list = FXCollections.observableArrayList();


    /**
     * Default Controller necessary for loading fxml files.
     */
    public SettingsController() {

    }

    @FXML
    public void initialize() {
        settingsTab.setGraphic(new ObserverLabel("settings"));
        permissionTab.setGraphic(new ObserverLabel("permissions"));
        registrationTab.setGraphic(new ObserverLabel("registration"));


        fillTreeTableView();
        this.setSettingsPane(new SettingsPane());


        permissionPane = loadPermissionPane();
        permissionPane.setVisible(false);

        this.permissionPaneParent.getChildren().addAll(permissionPane);

        this.tabPane.widthProperty().addListener(this::onPaneWidthChange);
        onPaneWidthChange(null, null, null);
        this.tabPane.getStyleClass().addAll("detail-menu");


        this.setRegistrationPane();

        try {
            Registries.getUnitRegistry().addDataObserver((observable, unitRegistryData) -> {

                        List<UnitConfigType.UnitConfig> unitConfigList = Registries.getUnitRegistry()
                                .getUnitConfigs();
                        Platform.runLater(() -> fillTable(unitConfigList));
                    }
            );
        } catch (CouldNotPerformException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private <T> void onPaneWidthChange(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        double width = this.tabPane.getWidth();
        double childrenCount = settingsTab.getTabPane().getTabs().size();

        settingsTab.getTabPane().setTabMinWidth(width / (childrenCount + 1));
        //+1 cause otherwise tabs would overlap with Floating Button 
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

        this.typeColumn = createJFXTreeTableColumn("Type",
                (unit) -> unit.getUnit().getType().name());
        this.typeColumn.setPrefWidth(150);

        unitsTable.getColumns().addAll(this.typeColumn, labelColumn, descColumn);

        RecursiveTreeItem<RecursiveUnitConfig> item = new RecursiveTreeItem<>(
                list, RecursiveTreeObject::getChildren);
        unitsTable.setRoot(item);

        unitsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener(this::onSelectionChange)
        ;


        filterInput.textProperty().addListener((o, oldVal, newVal) -> {
            unitsTable.setPredicate(
                    user -> user.getValue().getUnit().getLabel().toLowerCase().contains(newVal.toLowerCase())
                            || user.getValue().getUnit().getDescription().toLowerCase().contains(newVal.toLowerCase())
                            || user.getValue().getUnit().getType().name().toLowerCase().contains(newVal.toLowerCase()));
        });


    }


    private void onSelectionChange(javafx.beans.Observable observable, TreeItem oldValue, TreeItem newValue) {
        if (nonNull(newValue) && newValue.getValue() instanceof RecursiveUnitConfig) {
            setPermissionPaneVisible(true);
            permissionPaneController.setUnitConfig(((RecursiveUnitConfig) newValue.getValue()).getUnit());
        } else {
            setPermissionPaneVisible(false);
        }
    }

    private void setPermissionPaneVisible(boolean visible) {
        permissionPane.setVisible(visible);


    }

    private void fillTable(List<UnitConfigType.UnitConfig> unitConfigList) {

        unitsTable.unGroup(this.typeColumn);

        //TODO: nicht ganze Tabelle ersetzten, sondern nur geänderte Units
        // RecursiveUnitConfig.unit-Property nutzen?

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

    private <S, T> JFXTreeTableColumn<S, T> createJFXTreeTableColumn(String text, Function<S, T> supplier) {
        JFXTreeTableColumn<S, T> column = new JFXTreeTableColumn<>(text);
        column.setCellValueFactory(new MethodRefCellValueFactory<>(supplier, column));
        return column;
    }

    public SettingsPane getSettingsPane() {
        return settingsPane;
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

    private AnchorPane loadPermissionPane() {
        try {
            URL url = getClass().getClassLoader().getResource("PermissionPane.fxml");
            if (url == null) {
                throw new RuntimeException("PermissionPane.fxml not found");
            }

            FXMLLoader loader = new FXMLLoader(url);
            AnchorPane anchorPane = loader.load();
            this.permissionPaneController = loader.getController();

            anchorPane.getStyleClass().addAll("detail-menu");

            return anchorPane;
        } catch (IOException ex) {
            ex.printStackTrace();
            ExceptionPrinter.printHistory("Content could not be loaded", ex, LOGGER);
            throw new UncheckedIOException(ex);
        }
    }

    private void setRegistrationPane() {
        registrationController = new RegistrationController();
        registrationTab.setContent(registrationController.getRoot());
    }

    public class RecursiveUnitConfig extends RecursiveTreeObject<RecursiveUnitConfig> {

        final private SimpleObjectProperty<UnitConfigType.UnitConfig> unit = new SimpleObjectProperty<>();

        public RecursiveUnitConfig(UnitConfigType.UnitConfig unitConfig) {
            this.setUnit(unitConfig);
        }

        public UnitConfigType.UnitConfig getUnit() {
            return unit.get();
        }

        public void setUnit(UnitConfigType.UnitConfig unitConfig) {
            this.unit.set(Objects.requireNonNull(unitConfig));

        }

        public SimpleObjectProperty unitProperty() {
            return unit;
        }

    }

    private class MethodRefCellValueFactory<S, T> implements Callback<TreeTableColumn.CellDataFeatures<S, T>,
            ObservableValue<T>> {

        Function<S, T> supplier;

        JFXTreeTableColumn<S, T> column;

        public MethodRefCellValueFactory(Function<S, T> supplier, JFXTreeTableColumn<S, T> column) {
            this.supplier = Objects.requireNonNull(supplier);
            this.column = Objects.requireNonNull(column);
        }

        @Override
        public ObservableValue<T> call(TreeTableColumn.CellDataFeatures<S, T> param) {
            if (column.validateValue(param)) {
                return new SimpleObjectProperty(supplier.apply(param.getValue().getValue()));
            }
            return column.getComputedValue(param);
        }
    }
}





