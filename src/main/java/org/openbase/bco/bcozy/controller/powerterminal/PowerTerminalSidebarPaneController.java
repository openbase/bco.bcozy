package org.openbase.bco.bcozy.controller.powerterminal;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.DateRange;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Granularity;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Unit;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.VisualizationType;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.model.powerterminal.ChartStateModel;
import org.openbase.bco.bcozy.view.powerterminal.LocalizedCellFactory;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.jul.visual.javafx.control.AbstractFXController;
import org.openbase.type.domotic.unit.UnitConfigType;
import org.openbase.type.domotic.unit.UnitTemplateType;
import org.openbase.type.domotic.unit.location.LocationConfigType;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the power terminal sidebar pane, handling and creating various bindings for the chartStateModel.
 */
public class PowerTerminalSidebarPaneController extends AbstractFXController {

    public static final ZoneId TIME_ZONE_ID = ZoneId.of("GMT+2");
    public static final String DATE_ERROR_MESSAGE_IDENTIFIER = "powerterminal.dateErrorMessage";
    public static final String DATE_NOW_CHECKBOX_DESCRIPTION_IDENTIFIER = "powerterminal.dateNowCheckboxDescription";
    public static final String GLOBAL_CONSUMPTION_CHECKBOX_DESCRIPTION_IDENTIFIER = "powerterminal.overallConsumptionCheckboxDescription";

    @FXML
    private JFXComboBox<VisualizationType> selectVisualizationTypeBox;
    @FXML
    private JFXComboBox<Unit> selectUnitBox;
    @FXML
    public JFXCheckBox globalConsumptionCheckBox;
    @FXML
    public Text globalConsumptionCheckboxDescription;
    @FXML
    public VBox granularSelectionGroupVbox;
    @FXML
    public VBox globalConsumptionGroupHbox;
    @FXML
    public JFXComboBox selectRoomBox;
    @FXML
    public JFXComboBox selectConsumerBox;
    @FXML
    public Text consumerErrorMessage;
    @FXML
    public VBox dateSelectionGroupVbox;
    @FXML
    public HBox dateNowGroupHbox;
    @FXML
    private JFXCheckBox dateNowCheckBox;
    @FXML
    private JFXDatePicker selectStartDatePicker;
    @FXML
    private JFXDatePicker selectEndDatePicker;
    @FXML
    private Text dateErrorMessage;
    @FXML
    private Text dateNowCheckboxDescription;

    private ObjectProperty<DateRange> dateRange = new SimpleObjectProperty<>();
    private ObjectProperty<Granularity> granularity = new SimpleObjectProperty<>();
    private ChartStateModel chartStateModel;


    @Override
    public void updateDynamicContent() throws CouldNotPerformException {

    }

    @Override
    public void initContent() throws InitializationException {
        LocalizedCellFactory cellFactory = new LocalizedCellFactory<>(item -> LanguageSelection.getProperty(item.toString()));
        setupComboBox(cellFactory, selectVisualizationTypeBox, VisualizationType.getSelectableTypes(), 2);
        setupComboBox(cellFactory, selectUnitBox, Unit.values(), 1);


        selectStartDatePicker.disableProperty().bind(dateNowCheckBox.selectedProperty());
        selectEndDatePicker.disableProperty().bind(dateNowCheckBox.selectedProperty());
        selectStartDatePicker.setValue(LocalDate.now(TIME_ZONE_ID).minusDays(1));
        selectEndDatePicker.setValue(LocalDate.now(TIME_ZONE_ID));
        dateRange.set(new DateRange(selectStartDatePicker.getValue(), selectEndDatePicker.getValue()));

        chartStateModel = new ChartStateModel(selectVisualizationTypeBox.valueProperty(), selectUnitBox.valueProperty(),
                granularity, dateRange);

        setupGranularitySelection();
        setupDateSelection();
    }

    private void setupGranularitySelection() {
        globalConsumptionCheckboxDescription.textProperty().bind(LanguageSelection.getProperty(GLOBAL_CONSUMPTION_CHECKBOX_DESCRIPTION_IDENTIFIER));

        granularSelectionGroupVbox.managedProperty().bind(granularSelectionGroupVbox.visibleProperty());
        granularSelectionGroupVbox.visibleProperty().bind(globalConsumptionCheckBox.selectedProperty().not());
        List<UnitConfigType.UnitConfig> rooms = new ArrayList<>();
        try {
            rooms = Registries.getUnitRegistry().getUnitConfigs(UnitTemplateType.UnitTemplate.UnitType.LOCATION);
        } catch (CouldNotPerformException e) {
            e.printStackTrace();
        }
        rooms.removeIf(unit -> unit.getLocationConfig().getLocationType() != LocationConfigType.LocationConfig.LocationType.TILE);
        LocalizedCellFactory<UnitConfigType.UnitConfig> cellFactory
                = new LocalizedCellFactory<UnitConfigType.UnitConfig>(unit
                -> LanguageSelection.getProperty(unit.getLabel(), translatable
                -> LabelProcessor.getBestMatch(translatable, "Label not Found!")));
        setupComboBox(cellFactory, selectRoomBox, rooms.toArray(new UnitConfigType.UnitConfig[]{}), 0);

//        selectRoomBox.selectionModelProperty().addListener((source, old, newValue) -> {
//            List<UnitConfigType.UnitConfig> consumers = new ArrayList<>();
//            try {
//                consumers =
//                        Registries.getUnitRegistry()
//                                .getUnitConfigs(UnitTemplateType.UnitTemplate.UnitType.POWER_CONSUMPTION_SENSOR);
//                List<UnitConfigType.UnitConfig> locationUnits =
//                        Registries.getUnitRegistry()
//                                .getUnitConfigsByLocation(((UnitConfigType.UnitConfig) newValue).getId());
//                consumers.retainAll(locationUnits);
//            } catch (CouldNotPerformException e) {
//                e.printStackTrace();
//            }
//            LocalizedUnitCellFactory<UnitConfigType.UnitConfig> consumerUnitCellFactory = new LocalizedUnitCellFactory<>();
//            setupComboBox(consumerUnitCellFactory, selectConsumerBox, consumers.toArray(new UnitConfigType.UnitConfig[]{}), 0);
//        });


        consumerErrorMessage.textProperty().bind(LanguageSelection.getProperty("powerterminal.consumerErrorMessage"));

        globalConsumptionCheckBox.selectedProperty().set(true);
    }

    private void setupDateSelection() {
        selectStartDatePicker.valueProperty().addListener((source, old, newStartDate) -> {
            DateRange dateRange = new DateRange(newStartDate, this.dateRange.get().getEndDate());
            if (dateRange.isValid())
                this.dateRange.set(dateRange);
        });
        selectEndDatePicker.valueProperty().addListener((source, old, newEndDate) -> {
            DateRange dateRange = new DateRange(this.dateRange.get().getStartDate(), newEndDate);
            if (dateRange.isValid())
                this.dateRange.set(dateRange);
        });

        dateNowCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                dateRange.set(new DateRange());
            } else {
                DateRange dateRange = new DateRange(selectStartDatePicker.getValue(), selectEndDatePicker.getValue());
                if (dateRange.isValid()) {
                    this.dateRange.set(dateRange);
                }
            }
        });

        BooleanBinding dateValid = Bindings.createBooleanBinding(
                () -> {
                    DateRange dateRange = new DateRange(selectStartDatePicker.getValue(), selectEndDatePicker.getValue());
                    return dateRange.isValid() && !dateRange.isEmpty();
                }
                , selectStartDatePicker.valueProperty(), selectEndDatePicker.valueProperty());
        BooleanBinding multipleDataOk = Bindings.createBooleanBinding(
                () -> VisualizationType.canDisplayMultipleData(selectVisualizationTypeBox.getSelectionModel().getSelectedItem()),
                selectVisualizationTypeBox.valueProperty());
        BooleanBinding singleDataOk = Bindings.createBooleanBinding(
                () -> VisualizationType.canDisplaySingleton(selectVisualizationTypeBox.getSelectionModel().getSelectedItem()),
                selectVisualizationTypeBox.valueProperty());

        dateErrorMessage.textProperty().bind(LanguageSelection.getProperty(DATE_ERROR_MESSAGE_IDENTIFIER));
        dateErrorMessage.visibleProperty().bind(dateValid.not());

        dateNowCheckboxDescription.textProperty().bind(LanguageSelection.getProperty(DATE_NOW_CHECKBOX_DESCRIPTION_IDENTIFIER));
        dateNowGroupHbox.managedProperty().bind(dateNowGroupHbox.visibleProperty());
        dateNowGroupHbox.visibleProperty().bind(singleDataOk);
        dateNowCheckBox.disableProperty().bind(multipleDataOk.not());
        multipleDataOk.addListener(
                (observable, oldValue, newValue) -> {
                    if (!newValue) {
                        dateNowCheckBox.selectedProperty().set(true);
                    } else {
                        dateNowCheckBox.selectedProperty().set(false);
                    }
                });

        selectStartDatePicker.managedProperty().bind(selectStartDatePicker.visibleProperty());
        selectEndDatePicker.managedProperty().bind(selectEndDatePicker.visibleProperty());
        selectStartDatePicker.visibleProperty().bind(multipleDataOk);
        selectEndDatePicker.visibleProperty().bind(multipleDataOk);

    }

//        Registries.getUnitRegistry().getUnitConfigsByUnitType(UnitTemplateType.UnitTemplate.UnitType.POWER_CONSUMPTION_SENSOR); mit .getID bekommt man Unit ID die man in DB werfen kann
//        Registries.getUnitRegistry().getUnitConfigsByUnitType(UnitTemplateType.UnitTemplate.UnitType.LOCATION);

    /**
     * Fills a ComboBox with custom cells.
     *
     * @param <T>         Type contained by the custom cells
     * @param cellFactory
     * @param comboBox    ComboBox that will be set up
     * @param items       Items to fill the ComboBox with
     * @param index       Per Default selected cell index
     */
    private <T> void setupComboBox(Callback<ListView<T>, ListCell<T>> cellFactory, ComboBox<T> comboBox, T[] items, int index) {
        comboBox.setButtonCell(cellFactory.call(null));
        comboBox.setCellFactory(cellFactory);
        comboBox.getItems().addAll(items);
        comboBox.getSelectionModel().select(index);
    }

    public ChartStateModel getChartStateModel() {
        return chartStateModel;
    }

}
