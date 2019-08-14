package org.openbase.bco.bcozy.controller.powerterminal.chartattributes;

/**
 * Different types to visualize the power draw.
 */
public enum VisualizationType {
    BAR_CHART, PIE_CHART, WEBVIEW, LINE_CHART, TREE_CHART, HEATMAP;

    public static VisualizationType[] getSelectableTypes() {
        return new VisualizationType[]{BAR_CHART, PIE_CHART, LINE_CHART, TREE_CHART, HEATMAP};
    }

    /**
     * Describes if the VisualizationType can display single values.
     * @param visualizationType VisualizationType in question
     * @return Boolean describing if the VisualizationType can do so
     */
    public static boolean canDisplaySingleton(VisualizationType visualizationType) {
        switch (visualizationType) {
            case PIE_CHART: case TREE_CHART:
                return true;
            default:
                return false;
        }
    }

    /**
     * Describes if the VisualizationType can display multiple values.
     * @param visualizationType VisualizationType in question
     * @return Boolean describing if the VisualizationType can do so
     */
    public static boolean canDisplayMultipleData(VisualizationType visualizationType) {
        switch (visualizationType) {
            case BAR_CHART: case LINE_CHART:
                return true;
            default:
                return false;
        }

    }

    /**
     * Describes if the VisualizationType can display multiple sets of values.
     * @param visualizationType VisualizationType in question
     * @return Boolean describing if the VisualizationType can do so
     */
    public static boolean canDisplayMultipleGraphs(VisualizationType visualizationType) {
        switch (visualizationType) {
            case LINE_CHART: case BAR_CHART:
                return true;
            default:
                return false;
        }
    }

}
