package org.openbase.bco.bcozy.controller.powerterminal.chartattributes;

import org.openbase.bco.bcozy.model.LanguageSelection;

public enum VisualizationType {
    BARCHART, PIECHART, HEATMAP, LINECHART;

    public static VisualizationType[] getSelectableTypes() {
        return new VisualizationType[]{BARCHART, PIECHART, LINECHART, HEATMAP};
    }

}
