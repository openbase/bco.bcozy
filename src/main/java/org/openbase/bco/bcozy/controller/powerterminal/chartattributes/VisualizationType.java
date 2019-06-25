package org.openbase.bco.bcozy.controller.powerterminal.chartattributes;

import org.openbase.bco.bcozy.model.LanguageSelection;

public enum VisualizationType {
    BARCHART, PIECHART, WEBVIEW, LINECHART;

    public static VisualizationType[] getSelectableTypes() {
        return new VisualizationType[]{BARCHART, PIECHART, LINECHART};
    }

    @Override
    public String toString() {
        return LanguageSelection.getLocalized(super.name());
    }
}
