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
import javafx.scene.text.Text;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.DateRange;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Granularity;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Unit;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.VisualizationType;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.model.powerterminal.ChartStateModel;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.visual.javafx.control.AbstractFXController;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Controller that controls the right sidebar of the power terminal
 */
public class PowerTerminalSidebarPaneController extends AbstractFXController {

    public static final ZoneId TIME_ZONE_ID = ZoneId.of("GMT+2");
    public static final String DATE_ERROR_MESSAGE_IDENTIFIER = "powerterminal.dateErrorMessage";
    public static final String DATE_NOW_CHECKBOX_DESCRIPTION_IDENTIFIER = "powerterminal.dateNowCheckboxDescription";
    @FXML
    private JFXComboBox<VisualizationType> selectVisualizationTypeBox;
    @FXML
    private JFXComboBox<Unit> selectUnitBox;
    @FXML
    private JFXComboBox<Granularity> selectGranularityBox;
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
    private ChartStateModel chartStateModel;


    @Override
    public void updateDynamicContent() throws CouldNotPerformException {

    }

    @Override
    public void initContent() throws InitializationException {
        setupComboBox(selectVisualizationTypeBox, VisualizationType.getSelectableTypes());
        setupComboBox(selectGranularityBox, Granularity.values());
        setupComboBox(selectUnitBox, Unit.values());

        selectStartDatePicker.disableProperty().bind(dateNowCheckBox.selectedProperty());
        selectEndDatePicker.disableProperty().bind(dateNowCheckBox.selectedProperty());
        selectStartDatePicker.setValue(LocalDate.now(TIME_ZONE_ID).minusDays(1));
        selectEndDatePicker.setValue(LocalDate.now(TIME_ZONE_ID));
        dateRange.set(new DateRange(selectStartDatePicker.getValue(), selectEndDatePicker.getValue()));

        chartStateModel = new ChartStateModel(selectVisualizationTypeBox.valueProperty(), selectUnitBox.valueProperty(),
                selectGranularityBox.valueProperty(), dateRange);

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
        BooleanBinding dateValid = Bindings.createBooleanBinding(() -> new DateRange(selectStartDatePicker.getValue(), selectEndDatePicker.getValue()).isValid(),
                selectStartDatePicker.valueProperty(), selectEndDatePicker.valueProperty());
        dateErrorMessage.textProperty().bind(LanguageSelection.getProperty(DATE_ERROR_MESSAGE_IDENTIFIER));
        dateErrorMessage.visibleProperty().bind(dateValid.not());

        dateNowCheckboxDescription.textProperty().bind(LanguageSelection.getProperty(DATE_NOW_CHECKBOX_DESCRIPTION_IDENTIFIER));
    }

    private <T extends Enum> void setupComboBox(ComboBox<T> comboBox, T[] items) {
        LocalizedEnumCellFactory<T> cellFactory = new LocalizedEnumCellFactory<>(LanguageSelection::getProperty);
        comboBox.setButtonCell(cellFactory.call(null));
        comboBox.setCellFactory(cellFactory);
        comboBox.getItems().addAll(items);
        comboBox.getSelectionModel().select(0);
    }

    public ChartStateModel getChartStateModel() {
        return chartStateModel;
    }

}
