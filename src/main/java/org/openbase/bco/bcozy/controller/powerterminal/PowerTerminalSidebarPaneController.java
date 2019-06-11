package org.openbase.bco.bcozy.controller.powerterminal;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.openbase.bco.bcozy.controller.ContextMenuController;
import org.openbase.bco.bcozy.view.UnitMenu;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.visual.javafx.control.AbstractFXController;

public class PowerTerminalSidebarPaneController extends AbstractFXController {

    @FXML
    private ComboBox<ContextMenuController.energyChart> changeVisualizationBox;


    @Override
    public void updateDynamicContent() throws CouldNotPerformException {

    }

    @Override
    public void initContent() throws InitializationException {
        changeVisualizationBox.getItems().addAll(ContextMenuController.energyChart.values());
    }

    public void init(UnitMenu parentView) {
        changeVisualizationBox.valueProperty().bindBidirectional(parentView.getEnergyChartProperty());
    }

}
