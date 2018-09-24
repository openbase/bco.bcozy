package org.openbase.bco.bcozy.controller.group;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.openbase.bco.bcozy.controller.ButtonTableCellFactory;
import org.openbase.bco.bcozy.controller.Dialog;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.util.AuthorizationGroups;
import org.openbase.bco.bcozy.view.InfoPane;
import org.openbase.bco.bcozy.view.ObserverButton;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.ExceptionProcessor;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.visual.javafx.JFXConstants;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.language.LabelType.Label;
import rst.domotic.unit.UnitConfigType;

import java.util.List;

/**
 * @author vdasilva
 */
public class AuthorizationGroupController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationGroupController.class);


    @FXML
    private ObserverButton saveButton;
    @FXML
    private TextField label;
    @FXML
    private TableColumn<UnitConfigType.UnitConfig, String> removeColumn;
    @FXML
    private TableColumn<UnitConfigType.UnitConfig, Label> nameColumn;
    @FXML
    private TableView<UnitConfigType.UnitConfig> groupsTable;

    @FXML
    private AuthorizationGroupUsersController authorizationGroupUsersController;
    @FXML
    private VBox authorizationGroupUsers;

    @FXML
    private Pane root;

    private ObservableList<UnitConfigType.UnitConfig> groups = AuthorizationGroups.getAuthorizationGroups();

    private UnitConfigType.UnitConfig lastSelectedUnit;

    @FXML
    public void initialize() {

        saveButton.setApplyOnNewText(String::toUpperCase);

        nameColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getLabel()));
        LanguageSelection.addObserverFor("groupLabel", (locale, text) -> nameColumn.setText(text));

        removeColumn.setCellFactory(new ButtonTableCellFactory<>(
                (group, cellIndex) -> removeGroup(group),
                () -> new SVGGlyphIcon(FontAwesomeIcon.TIMES, JFXConstants.ICON_SIZE_EXTRA_SMALL, true)
        ));

        groupsTable.widthProperty().addListener((observable, oldValue, newValue) ->
                nameColumn.setPrefWidth(newValue.doubleValue() - removeColumn.getWidth() - 2)
        );

        groupsTable.setEditable(false);
        nameColumn.setResizable(false);
        removeColumn.setResizable(false);
        groupsTable.setItems(groups);

        authorizationGroupUsersController.selectedGroupProperty().bind(groupsTable.getSelectionModel().selectedItemProperty());

        LanguageSelection.addObserverFor("groupLabel", (locale, text) -> label.setPromptText(text));

        AuthorizationGroups.addListObserver(this::showGroups);
    }

    /**
     * Displays the groups in the group-table.
     *
     * @param groups the groups to show
     */
    private void showGroups(List<UnitConfigType.UnitConfig> groups) {
        if (groupsTable.getSelectionModel().getSelectedItem() != null) {
            lastSelectedUnit = groupsTable.getSelectionModel().getSelectedItem();
        }
        groupsTable.setItems(FXCollections.observableArrayList(groups));

        reselectLastUnit();
    }


    /**
     * Selects the last selected unit. Used after a list-update, which deselects the unit.
     */
    private void reselectLastUnit() {
        if (lastSelectedUnit != null) {
            String id = lastSelectedUnit.getId();
            for (UnitConfigType.UnitConfig group : groups) {
                if (group.getId().equals(id)) {
                    groupsTable.getSelectionModel().select(group);
                    return;
                }
            }
        }
        groupsTable.getSelectionModel().select(null);

    }

    public Pane getRoot() {
        return root;
    }

    private void removeGroup(UnitConfigType.UnitConfig group) {
        if (!Dialog.getConfirmation("removeGroup.confirmation", group.getLabel())) {
            return;
        }

        try {
            AuthorizationGroups.removeAuthorizationGroup(group);
            InfoPane.info("deleteSuccess")
                    .backgroundColor(Color.GREEN)
                    .hideAfter(Duration.seconds(5));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);

            String message = null;
            try {
                message = LanguageSelection.getLocalized("deleteErrorWithMessage", ExceptionProcessor.getInitialCauseMessage(ex));
            } catch (NotAvailableException e) {
                message = "Unknown Error";
            }

            InfoPane.info(message)
                    .backgroundColor(Color.RED)
                    .hideAfter(Duration.seconds(5));
        }
    }

    @FXML
    private void addGroup() {

        try {
            lastSelectedUnit = AuthorizationGroups.addAuthorizationGroup(label.getText());
            groupsTable.getSelectionModel().select(null);
            label.clear();
            InfoPane.info("saveSuccess")
                    .backgroundColor(Color.GREEN)
                    .hideAfter(Duration.seconds(5));

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();

        } catch (CouldNotPerformException ex) {
            label.getStyleClass().add("text-field-wrong");

            ExceptionPrinter.printHistory(ex, LOGGER);

            String message = null;
            try {
                message = LanguageSelection.getLocalized("saveErrorWithMessage", ExceptionProcessor.getInitialCauseMessage(ex));
            } catch (NotAvailableException e) {
                message = "Unknown Error";
            }

            InfoPane.info(message)
                    .backgroundColor(Color.RED)
                    .hideAfter(Duration.seconds(5));
        }

    }

}
