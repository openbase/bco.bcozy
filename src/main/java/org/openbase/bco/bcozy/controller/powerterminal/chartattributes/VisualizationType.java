package org.openbase.bco.bcozy.controller.powerterminal.chartattributes;


public enum VisualizationType {
    BAR_CHART, PIE_CHART, WEBVIEW, LINE_CHART, TREE_CHART;

    public static VisualizationType[] getSelectableTypes() {
        return new VisualizationType[]{BAR_CHART, PIE_CHART, LINE_CHART};
    }

    public static boolean canDisplaySingleton(VisualizationType visualizationType) {
        switch (visualizationType) {
            case PIE_CHART: case TREE_CHART:
                return true;
            default:
                return false;
        }
    }

    public static boolean canDisplayMultipleData(VisualizationType visualizationType) {
        switch (visualizationType) {
            case BAR_CHART: case LINE_CHART:
                return true;
            default:
                return false;
        }

    }

}
