package org.openbase.bco.bcozy.controller.powerterminal;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Granularity;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.TimeSpan;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.Unit;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.VisualizationType;
import org.openbase.bco.bcozy.view.UnitMenu;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.visual.javafx.control.AbstractFXController;

public class PowerTerminalSidebarPaneController extends AbstractFXController {

    @FXML
    private JFXComboBox<VisualizationType> selectVisualizationTypeBox;
    @FXML
    private JFXComboBox<Unit> selectUnitBox;
    @FXML
    private JFXComboBox<Granularity> selectGranularityBox;
    @FXML
    private JFXComboBox<TimeSpan> selectTimeSpanBox;


    @Override
    public void updateDynamicContent() throws CouldNotPerformException {

    }

    @Override
    public void initContent() throws InitializationException {
        selectVisualizationTypeBox.getItems().addAll(VisualizationType.values());
        selectGranularityBox.getItems().addAll(Granularity.values());
        selectTimeSpanBox.getItems().addAll(TimeSpan.values());
        selectUnitBox.getItems().addAll(Unit.values());
    }

    public void init(UnitMenu parentView) {
        selectVisualizationTypeBox.valueProperty().bindBidirectional(parentView.getEnergyChartProperty());
    }

}
