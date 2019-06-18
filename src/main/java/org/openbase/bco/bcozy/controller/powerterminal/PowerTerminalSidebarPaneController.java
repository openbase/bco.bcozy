package org.openbase.bco.bcozy.controller.powerterminal;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Granularity;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Unit;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.VisualizationType;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.visual.javafx.control.AbstractFXController;

import java.time.LocalDate;

public class PowerTerminalSidebarPaneController extends AbstractFXController {

    @FXML
    private JFXComboBox<VisualizationType> selectVisualizationTypeBox;
    @FXML
    private JFXComboBox<Unit> selectUnitBox;
    @FXML
    private JFXComboBox<Granularity> selectGranularityBox;
    @FXML
    private JFXCheckBox selectNowCheckBox;
    @FXML
    private JFXDatePicker selectStartDatePicker;
    @FXML
    private JFXDatePicker selectEndDatePicker;


    @Override
    public void updateDynamicContent() throws CouldNotPerformException {

    }

    @Override
    public void initContent() throws InitializationException {
        selectVisualizationTypeBox.getItems().addAll(VisualizationType.values());
        selectGranularityBox.getItems().addAll(Granularity.values());
        selectUnitBox.getItems().addAll(Unit.values());
        selectStartDatePicker.disableProperty().bind(selectNowCheckBox.selectedProperty());
        selectEndDatePicker.disableProperty().bind(selectNowCheckBox.selectedProperty());
    }

    public void init() {

    }

    public ObjectProperty<VisualizationType> getVisualizationTypeProperty() {
        return selectVisualizationTypeBox.valueProperty();
    }

    public ObjectProperty<LocalDate> getStartDateProperty() {
        return selectStartDatePicker.valueProperty();
    }

    public ObjectProperty<LocalDate> getEndDateProperty() {
        return selectEndDatePicker.valueProperty();
    }
}
