package org.openbase.bco.bcozy.controller.powerterminal.chartcontroller;

import javafx.scene.Node;
import org.openbase.bco.bcozy.model.powerterminal.ChartStateModel;
import org.openbase.bco.bcozy.view.powerterminal.TilesFxView;

public interface ChartController {

    void init(ChartStateModel chartStateModel);

    void enableDataRefresh(long interval, ChartStateModel chartStateModel);

    void updateChart(ChartStateModel chartStateModel);

    Node getView();
}
