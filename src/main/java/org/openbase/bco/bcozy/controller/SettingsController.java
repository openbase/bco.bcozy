package org.openbase.bco.bcozy.controller;

import com.jfoenix.controls.JFXTreeTableView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.openbase.bco.bcozy.BCozy;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.permissions.RecursiveUnitConfig;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ForegroundPane;
import org.openbase.bco.bcozy.view.ObserverLabel;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author vdasilva
 */
public class SettingsController {

    /**
     * Application Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MainMenuController.class);
    public Accordion adminAccordion;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab settingsTab;

    @FXML
    private Tab permissionTab;

    @FXML
    private JFXTreeTableView<RecursiveUnitConfig> unitsTable;

    @FXML
    private VBox permissionPaneParent;


    private UserSettingsController userSettingsController;


    /**
     * @deprecated Use {@link #SettingsController()} instead.
     */
    @Deprecated
    public SettingsController(ForegroundPane foregroudPane) {
    }

    public SettingsController() {
    }

    @FXML
    public void initialize() {
        settingsTab.setGraphic(new ObserverLabel("settings"));
        permissionTab.setGraphic(new ObserverLabel("permissions"));


        Pane userSettingsPane = loadUserSettingsPane();
        settingsTab.setContent(userSettingsPane);


        Pair<? extends Parent, ?> permissionPane = loadPermissionPane();
        permissionTab.setContent(permissionPane.getKey());


        this.tabPane.widthProperty().addListener(this::onPaneWidthChange);
        onPaneWidthChange(null, null, null);
        this.tabPane.getStyleClass().addAll("detail-menu");


        TitledPane registrationPane = new TitledPane("userManagement", this.loadRegistrationPane());
        LanguageSelection.addObserverFor("userManagement", registrationPane::setText);
        this.adminAccordion.getPanes().add(registrationPane);

        TitledPane groupsPane = new TitledPane("usergroups", this.loadGroupsPane());
        LanguageSelection.addObserverFor("usergroups", groupsPane::setText);
        this.adminAccordion.getPanes().add(groupsPane);
    }

    private URL getFxmlURL(String filename) throws NullPointerException {
        return Objects.requireNonNull(getClass().getClassLoader().getResource(filename),
                filename + " not found");
    }

    private Pane loadUserSettingsPane() {
        try {
            URL url = getFxmlURL("UserSettingsPane.fxml");

            FXMLLoader loader = new FXMLLoader(url);
            Pane root = loader.load();

            this.userSettingsController = loader.getController();

            this.userSettingsController.getThemeChoice().setOnAction(event -> chooseTheme());

            //Necessary to ensure that the first change is not missed by the ChangeListener
            this.userSettingsController.getThemeChoice().getSelectionModel().select(0);

            return root;
        } catch (IOException ex) {
            ExceptionPrinter.printHistory("Content could not be loaded", ex, LOGGER);
            throw new UncheckedIOException(ex);
        }


    }


    private <T> void onPaneWidthChange(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        double width = this.tabPane.getWidth();
        double childrenCount = settingsTab.getTabPane().getTabs().size();

        settingsTab.getTabPane().setTabMinWidth(width / (childrenCount + 1));
        //+1 cause otherwise tabs would overlap with Floating Button 
    }

    public UserSettingsController getUserSettingsController() {
        return userSettingsController;
    }

    private void chooseTheme() {
        final ResourceBundle languageBundle = ResourceBundle
                .getBundle(Constants.LANGUAGE_RESOURCE_BUNDLE, Locale.getDefault());

        userSettingsController.getThemeChoice().getSelectionModel().selectedIndexProperty()
                .addListener(new ChangeListener<Number>() {

                    @Override
                    public void changed(final ObservableValue<? extends Number> observableValue, final Number number, final Number number2) {
                        if (userSettingsController.getAvailableThemes().get(number2.intValue())
                                .equals(languageBundle.getString(Constants.LIGHT_THEME_CSS_NAME))) {
                            BCozy.changeTheme(Constants.LIGHT_THEME_CSS);
                        } else if (userSettingsController.getAvailableThemes().get(number2.intValue())
                                .equals(languageBundle.getString(Constants.DARK_THEME_CSS_NAME))) {
                            BCozy.changeTheme(Constants.DARK_THEME_CSS);
                        }
                    }
                });
    }

    private <VIEW extends Parent, CONTROLLER> Pair<VIEW, CONTROLLER> loadPermissionPane() {
        try {
            URL url = getFxmlURL("view/permissions/PermissionsPane.fxml");
            FXMLLoader loader = new FXMLLoader(url);

            VIEW anchorPane = loader.load();
            CONTROLLER controller = loader.getController();

            return new Pair<>(anchorPane, controller);
        } catch (IOException ex) {
            ExceptionPrinter.printHistory("Content could not be loaded", ex, LOGGER);
            throw new UncheckedIOException(ex);
        }
    }

    private Pane loadGroupsPane() {
        try {
            URL url = getFxmlURL("GroupsPane.fxml");

            FXMLLoader loader = new FXMLLoader(url);
            Pane anchorPane = loader.load();

            return anchorPane;
        } catch (IOException ex) {
            ExceptionPrinter.printHistory("Content could not be loaded", ex, LOGGER);
            throw new UncheckedIOException(ex);
        }
    }

    private Pane loadRegistrationPane() {
        try {
            URL url = Objects.requireNonNull(getClass().getClassLoader().getResource("Registration.fxml"),
                    "Registration.fxml not found");

            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory(clazz -> new UserManagementController());
            Pane root = loader.load();

            return root;

        } catch (IOException ex) {
            ExceptionPrinter.printHistory("Content could not be loaded", ex, LOGGER);
            throw new UncheckedIOException(ex);
        }
    }


}





