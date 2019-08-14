package org.openbase.bco.bcozy.model.powerterminal;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import org.openbase.bco.bcozy.controller.powerterminal.chartattributes.*;

/**
 * Stores the state of the displayed power draw chart.
 */
public class ChartStateModel {
    private ObjectProperty<VisualizationType> visualizationTypeProperty;
    private ObjectProperty<Unit> unitProperty;
    private ReadOnlyStringProperty selectedConsumerProperty;
    private ObjectProperty<DateRange> dateRangeProperty;

    public ChartStateModel(ObjectProperty<VisualizationType> visualizationTypeProperty, ObjectProperty<Unit> unitProperty,
                           ReadOnlyStringProperty selectedConsumerProperty, ObjectProperty<DateRange> dateRangeProperty) {
        this.visualizationTypeProperty = visualizationTypeProperty;
        this.unitProperty = unitProperty;
        this.selectedConsumerProperty = selectedConsumerProperty;
        this.dateRangeProperty = dateRangeProperty;
    }

    public VisualizationType getVisualizationType() {
        return visualizationTypeProperty.get();
    }

    public ObjectProperty<VisualizationType> visualizationTypeProperty() {
        return visualizationTypeProperty;
    }

    public Unit getUnit() {
        return unitProperty.get();
    }

    public ObjectProperty<Unit> unitProperty() {
        return unitProperty;
    }

    public String getSelectedConsumer() {
        return selectedConsumerProperty.get();
    }

    public ReadOnlyStringProperty selectedConsumerProperty() {
        return selectedConsumerProperty;
    }

    public DateRange getDateRange() {
        return dateRangeProperty.get();
    }

    public ObjectProperty<DateRange> dateRangeProperty() {
        return dateRangeProperty;
    }
}
