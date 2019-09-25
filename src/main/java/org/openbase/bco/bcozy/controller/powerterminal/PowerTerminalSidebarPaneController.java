package org.openbase.bco.bcozy.controller.powerterminal;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import eu.hansolo.fx.charts.heatmap.HeatMap;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.DateRange;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Unit;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.VisualizationType;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.model.powerterminal.ChartStateModel;
import org.openbase.bco.bcozy.model.powerterminal.PowerTerminalDBService;
import org.openbase.bco.bcozy.model.powerterminal.PowerTerminalRegistryService;
import org.openbase.bco.bcozy.view.powerterminal.LocalizedCellFactory;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.ExceptionProcessor;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import org.openbase.jul.visual.javafx.control.AbstractFXController;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.language.LabelType;
import org.slf4j.LoggerFactory;

import java.io.NotActiveException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the power terminal sidebar pane, handling and creating various bindings for the chartStateModel.
 */
public class PowerTerminalSidebarPaneController extends AbstractFXController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PowerTerminalSidebarPaneController.class);

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
    public JFXComboBox<UnitConfig> selectLocationBox;
    @FXML
    public JFXComboBox selectConsumerBox;
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

    private ObjectProperty<DateRange> dateRange = new SimpleObjectProperty<>(new DateRange());
    private ReadOnlyStringWrapper selectedUnitId = new ReadOnlyStringWrapper();
    private ChartStateModel chartStateModel;
    private Map<String, List<UnitConfig>> locationConsumerMap;
    private UnitConfig noConsumerSelectedDummyConfig;

    @Override
    public void updateDynamicContent() throws CouldNotPerformException {

    }

    @Override
    public void initContent() throws InitializationException {
        LocalizedCellFactory cellFactory = new LocalizedCellFactory<>(item -> LanguageSelection.getProperty(item.toString()));
        setupComboBox(cellFactory, selectVisualizationTypeBox, List.of(VisualizationType.getSelectableTypes()), 2);
        setupComboBox(cellFactory, selectUnitBox, List.of(Unit.values()), 1);

        BooleanBinding canDisplayDifferentUnits = Bindings.createBooleanBinding(
                () -> VisualizationType.canDisplayDifferentUnits(selectVisualizationTypeBox.getSelectionModel().getSelectedItem()),
                selectVisualizationTypeBox.valueProperty());
        hideNodeIf(canDisplayDifferentUnits, selectUnitBox);


        chartStateModel = new ChartStateModel(selectVisualizationTypeBox.valueProperty(), selectUnitBox.valueProperty(),
                selectedUnitId.getReadOnlyProperty(), dateRange);
        GlobalCachedExecutorService.submit(() -> {
            try {
                Registries.waitForData();
                setupGranularitySelection();
                setupDateSelection();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } catch (CouldNotPerformException ex) {
                if(!ExceptionProcessor.isCausedBySystemShutdown(ex)) {
                    ExceptionPrinter.printHistory("Could not instantiate CustomUnitPool", ex, logger);
                }
            }
        });
    }

    private void setupGranularitySelection() {
        locationConsumerMap = new HashMap<>();
        noConsumerSelectedDummyConfig = generateDummyUnitConfig(PowerTerminalDBService.UNIT_ID_LOCATION_CONSUMPTION,
                "-No Selection-", "-Keine Auswahl-");
        globalConsumptionCheckboxDescription.textProperty().bind(LanguageSelection.getProperty(GLOBAL_CONSUMPTION_CHECKBOX_DESCRIPTION_IDENTIFIER));
        globalConsumptionCheckBox.selectedProperty().addListener((source, old, newValue) -> {
            try {
                selectedUnitId.set(getSelectedConsumerId());
            } catch (NotAvailableException e) {
                // do nothing if selection is not available
            }
        });
        selectConsumerBox.valueProperty().addListener((source, old, newValue) -> {
            try {
                selectedUnitId.set(getSelectedConsumerId());
            } catch (NotAvailableException e) {
                // do nothing if selection is not available
            }
        });
        selectLocationBox.valueProperty().addListener((source, old, newValue) -> {
            //Consumers per location could be saved in a map to reduce number of registry accesses
            UnitConfig location = (UnitConfig) newValue;
            if (!locationConsumerMap.containsKey(location.getId())) {
                locationConsumerMap.put(location.getId(), PowerTerminalRegistryService.getConsumers(((UnitConfig) newValue).getId()));
                locationConsumerMap.get(location.getId()).add(0, noConsumerSelectedDummyConfig);
            }
            LocalizedCellFactory<UnitConfig> consumerUnitCellFactory
                    = new LocalizedCellFactory<>(unit
                    -> LanguageSelection.getProperty(unit.getLabel(), translatable
                    -> LabelProcessor.getBestMatch(translatable, "Label not Found!")));
            setupComboBox(consumerUnitCellFactory, selectConsumerBox, locationConsumerMap.get(location.getId()), 0);
        });

        granularSelectionGroupVbox.managedProperty().bind(granularSelectionGroupVbox.visibleProperty());
        granularSelectionGroupVbox.visibleProperty().bind(globalConsumptionCheckBox.selectedProperty().not());
        List<UnitConfig> locations = PowerTerminalRegistryService.getTileLocations();
        LocalizedCellFactory<UnitConfig> cellFactory
                = new LocalizedCellFactory<>(unit
                -> LanguageSelection.getProperty(unit.getLabel(), translatable
                -> LabelProcessor.getBestMatch(translatable, "Label not Found!")));
        setupComboBox(cellFactory, selectLocationBox, locations, 0);

        globalConsumptionCheckBox.selectedProperty().set(true);
    }

    private void setupDateSelection() {
        selectStartDatePicker.setValue(LocalDate.now(TIME_ZONE_ID).minusDays(1));
        selectEndDatePicker.setValue(LocalDate.now(TIME_ZONE_ID));
        dateRange.set(new DateRange(selectStartDatePicker.getValue(), selectEndDatePicker.getValue()));

        selectStartDatePicker.valueProperty().addListener((source, old, newStartDate) -> {
            DateRange dateRange = new DateRange(newStartDate, this.dateRange.get().getEndDate());
            if (dateRange.isValid()) {
                this.dateRange.set(dateRange);
            }
        });
        selectEndDatePicker.valueProperty().addListener((source, old, newEndDate) -> {
            DateRange dateRange = new DateRange(this.dateRange.get().getStartDate(), newEndDate);
            if (dateRange.isValid()) {
                this.dateRange.set(dateRange);
            }
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


        hideNodeIf(dateNowCheckBox.selectedProperty(), selectStartDatePicker);
        hideNodeIf(dateNowCheckBox.selectedProperty(), selectEndDatePicker);
        hideNodeIf(dateValid.not(), dateErrorMessage);
        hideNodeIf(multipleDataOk, selectStartDatePicker);
        hideNodeIf(multipleDataOk, selectEndDatePicker);
    }

    private void hideNodeIf(ObservableValue<Boolean> booleanObservableValue, Node node) {
        node.managedProperty().bind(node.visibleProperty());
        node.visibleProperty().bind(booleanObservableValue);
    }

    /**
     * Fills a ComboBox with custom cells.
     *
     * @param <T>         Type contained by the custom cells
     * @param cellFactory
     * @param comboBox    ComboBox that will be set up
     * @param items       Items to fill the ComboBox with
     * @param index       Per Default selected cell index
     */
    private <T> void setupComboBox
    (Callback<ListView<T>, ListCell<T>> cellFactory, ComboBox<T> comboBox, Collection<T> items,
     int index) {
        comboBox.setButtonCell(cellFactory.call(null));
        comboBox.setCellFactory(cellFactory);
        comboBox.getItems().setAll(items);
        if (index >= 0) {
            comboBox.getSelectionModel().select(index);
        }
    }

    private String getSelectedConsumerId() throws NotAvailableException {
        if (selectConsumerBox.valueProperty().get() == null) {
            throw new NotAvailableException("selection");
        }
        String selectedLocationUnitId = selectLocationBox.valueProperty().get().getId();
        if (globalConsumptionCheckBox.selectedProperty().get()) {
            return PowerTerminalDBService.UNIT_ID_GLOBAL_CONSUMPTION;
        }

        UnitConfig selectedConsumerUnitConfig = ((UnitConfig) selectConsumerBox.valueProperty().get());
        if (selectedConsumerUnitConfig == null) {
            return selectedLocationUnitId;
        }

        String selectedConsumerUnitId = selectedConsumerUnitConfig.getId();
        if (selectedConsumerUnitId.equals(PowerTerminalDBService.UNIT_ID_LOCATION_CONSUMPTION)) {
            return selectedLocationUnitId;
        }
        return selectedConsumerUnitId;
    }

    private UnitConfig generateDummyUnitConfig(String id, String labelEn, String labelDe) {
        return UnitConfig.newBuilder()
                .setId(id)
                .setLabel(LabelType.Label.newBuilder()
                        .addEntry(0, LabelType.Label.MapFieldEntry.newBuilder().setKey("en").addValue(labelEn).build())
                        .addEntry(1, LabelType.Label.MapFieldEntry.newBuilder().setKey("de").addValue(labelDe).build())
                        .build())
                .build();
    }

    public ChartStateModel getChartStateModel() {
        return chartStateModel;
    }

}
