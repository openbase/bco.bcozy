package org.openbase.bco.bcozy.view.pane.unit;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.openbase.bco.bcozy.BCozy;
import org.openbase.bco.bcozy.view.generic.EmphasisControlTrianglePane;
import org.openbase.bco.bcozy.view.location.LocationMapPane;
import org.openbase.bco.bcozy.view.mainmenupanes.PaneElement;
import org.openbase.bco.dal.remote.layer.unit.location.LocationRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.jul.pattern.Observer;
import org.openbase.jul.pattern.controller.ConfigurableRemote;
import org.openbase.jul.pattern.provider.DataProvider;
import org.openbase.jul.schedule.RecurrenceEventFilter;
import org.openbase.jul.visual.javafx.JFXConstants;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;
import org.openbase.type.domotic.state.EmphasisStateType.EmphasisState;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.location.LocationDataType.LocationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitMenuLocationPane extends PaneElement {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationMapPane.class);

    private final EmphasisControlTrianglePane emphasisControlTrianglePane;
    private final Label locationLabel;
    private final BorderPane statusIcon;
    private LocationRemote locationRemote;

    private RecurrenceEventFilter<EmphasisState> eventFilter;

    private Observer<DataProvider<LocationData>, LocationData> dataObserver;
    private Observer<ConfigurableRemote<String, LocationData, UnitConfig>, UnitConfig> configObserver;

    public UnitMenuLocationPane() {
        this.emphasisControlTrianglePane = new EmphasisControlTrianglePane();
        //this.emphasisControlTrianglePane.setAlignment(Pos.CENTER_LEFT);

        this.locationLabel = new Label("Select a Room");
        this.locationLabel.setAlignment(Pos.TOP_CENTER);
        this.locationLabel.getStyleClass().clear();
        this.locationLabel.getStyleClass().add("headline");

        this.statusIcon = new BorderPane(new SVGGlyphIcon(MaterialDesignIcon.ACCOUNT_CIRCLE, JFXConstants.ICON_SIZE_MIDDLE, true));

        this.getChildren().addAll(locationLabel, emphasisControlTrianglePane);

        this.eventFilter = new RecurrenceEventFilter<>() {
            @Override
            public void relay() throws Exception {
                locationRemote.setEmphasisState(eventFilter.getLatestValue());
            }
        };

        this.dataObserver = (source, data) -> {
            Platform.runLater(() -> {
                applyDataUpdate(data);
            });
        };

        this.configObserver = (source, config) -> {
            Platform.runLater(() -> {
                applyConfigUpdate(config);
            });
        };

        BCozy.selectedLocationProperty.addListener((observable, oldLocation, newLocation) -> {

            if (oldLocation != null) {
                oldLocation.removeConfigObserver(configObserver);
                oldLocation.removeDataObserver(dataObserver);
            }

            locationRemote = newLocation;

            newLocation.addConfigObserver(configObserver);
            newLocation.addDataObserver(dataObserver);

            try {
                applyConfigUpdate(newLocation.getConfig());
                applyDataUpdate(newLocation.getData());
            } catch (NotAvailableException ex) {
                LOGGER.error("Could not apply location update!", LOGGER);
            }
        });

        emphasisControlTrianglePane.emphasisStateProperty().addListener(new ChangeListener<EmphasisState>() {
            @Override
            public void changed(ObservableValue<? extends EmphasisState> observable, EmphasisState oldValue, EmphasisState newValue) {
                // skip if the updates was caused by the location remote itself.
                if (dataUpdateInProgress) {
                    return;
                }

                try {
                    eventFilter.trigger(newValue);
                } catch (CouldNotPerformException ex) {
                    ExceptionPrinter.printHistory("Could not forward emphasis state update>!", ex, LOGGER);
                }
            }
        });
    }

    /**
     * Getter Method for the Label.
     *
     * @return label
     */
    public Label getLocationLabel() {
        return locationLabel;
    }

    private boolean dataUpdateInProgress = false;

    public void applyDataUpdate(final LocationData locationData) {

        // ignore new incoming emphasis state events as long as there is currently a user moving the emphasis triangle handle.
        // this makes sure the handle is not randomly moving around.
        if(eventFilter.isFilterActive()) {
            return;
        }

        dataUpdateInProgress = true;
        emphasisControlTrianglePane.emphasisStateProperty().set(locationData.getEmphasisState());
        dataUpdateInProgress = false;
    }

    public void applyConfigUpdate(final UnitConfig unitConfig) {
        locationLabel.setText(LabelProcessor.getBestMatch(unitConfig.getLabel(), "?"));
    }

    @Override
    public Node getStatusIcon() {
        return statusIcon;
    }
}
