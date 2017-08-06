package org.openbase.bco.bcozy.view.pane.unit.location;

/**
 * ==================================================================
 *
 * This file is part of org.openbase.bco.bcozy.
 *
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
import org.openbase.bco.bcozy.view.pane.unit.*;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.scene.paint.Color;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.schedule.RecurrenceEventFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.Future;
import javafx.scene.layout.Pane;
import org.openbase.bco.bcozy.view.generic.ColorChooser;
import org.openbase.bco.bcozy.view.pane.unit.AbstractUnitPane;
import org.openbase.bco.dal.remote.unit.location.LocationRemote;
import org.openbase.jul.visual.javafx.transform.JFXColorToHSBColorTransformer;
import rst.domotic.state.PowerStateType.PowerState;
import rst.domotic.unit.location.LocationDataType.LocationData;

/**
 * Created by agatting on 03.12.15.
 */
public class LocationPane extends AbstractUnitPane<LocationRemote, LocationData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationPane.class);

    private ColorChooser colorChooser;

    private final RecurrenceEventFilter<Color> recurrenceEventFilterHSV = new RecurrenceEventFilter<Color>(Constants.RECURRENCE_EVENT_FILTER_MILLI_TIMEOUT) {


        @Override
        public void relay() {
            try {
                getUnitRemote().setColor(JFXColorToHSBColorTransformer.transform(getLastValue()));
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory("Could not send color update!", ex, LOGGER);
            }
        }
    };

    /**
     * Constructor for the Colorable Light Pane.
     *
     */
    public LocationPane() {
        super(LocationRemote.class, true);
        this.setIcon(MaterialDesignIcon.LIGHTBULB_OUTLINE, MaterialDesignIcon.LIGHTBULB);
    }

    @Override
    protected void initBodyContent(Pane bodyPane) throws CouldNotPerformException {
        colorChooser = new ColorChooser();
        colorChooser.initContent();
        colorChooser.selectedColorProperty().addListener((observable) -> {
            System.out.println("apply update "+colorChooser.getSelectedColor());
            if (isHover()) {
                System.out.println("..."+colorChooser.getSelectedColor());
                recurrenceEventFilterHSV.trigger(colorChooser.getSelectedColor());
            }
        });
        bodyPane.getChildren().add(colorChooser);
        
    }

    @Override
    public void updateDynamicContent() {
        super.updateDynamicContent();

        // detect power state
        PowerState.State state = PowerState.State.UNKNOWN;
        try {
            state = getUnitRemote().getData().getPowerState().getValue();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.DEBUG);
        }

        // detect color
        Color color;
        try {
            color = JFXColorToHSBColorTransformer.transform(getData().getColorState().getColor().getHsbColor());
        } catch (CouldNotPerformException e) {
            color = Color.TRANSPARENT;
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.DEBUG);
        }
        
//
//        if (colorChooser != null) {
//            System.out.println("set color");
//            colorChooser.setSelectedColor(color);
//        }

        switch (state) {
            case OFF:
                getIcon().setBackgroundIconColor(Color.TRANSPARENT);
                setInfoText("lightOff");
                primaryActivationProperty().setValue(Boolean.FALSE);
                break;
            case ON:
                getIcon().setBackgroundIconColor(color);
                setInfoText("lightOn");
                primaryActivationProperty().setValue(Boolean.TRUE);
                break;
            default:
                setInfoText("unknown");
                break;
        }
    }

    @Override
    protected Future applyPrimaryActivationUpdate(final boolean activation) throws CouldNotPerformException {
        
        return (activation) ? getUnitRemote().setPowerState(PowerState.State.ON) : getUnitRemote().setPowerState(PowerState.State.OFF);
    }
}
