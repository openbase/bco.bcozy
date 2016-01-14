/**
 * ==================================================================
 *
 * This file is part of org.dc.bco.bcozy.
 *
 * org.dc.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.dc.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.dc.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.dc.bco.bcozy.view.devicepanes;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import org.dc.bco.bcozy.view.SVGIcon;
import org.dc.bco.dal.remote.unit.DALRemoteService;
import org.dc.bco.dal.remote.unit.RollershutterRemote;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.pattern.Observable;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.dc.bco.bcozy.view.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.state.ShutterStateType;
import rst.homeautomation.unit.RollershutterType;

/**
 * Created by hoestreich on 11/19/15.
 */
public class RollershutterPane extends UnitPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(RollershutterPane.class);

    private final RollershutterRemote rollershutterRemote;
    private final BorderPane headContent;
    private final HBox bodyContent;
    private final SVGIcon rollerShutterIconBackground;
    private final SVGIcon rollerShutterIconForeground;
    private final Group rollerShutterIcon;
    private final Rectangle clip;

    /**
     * Constructor for a RollershutterPane.
     * @param rollerShutterRemote DALRemoteService
     */
    public RollershutterPane(final DALRemoteService rollerShutterRemote) {
        this.rollershutterRemote = (RollershutterRemote) rollerShutterRemote;

        rollerShutterIconBackground = new SVGIcon(MaterialDesignIcon.FORMAT_ALIGN_JUSTIFY, Constants.SMALL_ICON, false);
        rollerShutterIconForeground = new SVGIcon(MaterialDesignIcon.FORMAT_ALIGN_JUSTIFY, Constants.SMALL_ICON, false);
        rollerShutterIcon = new Group();
        clip = new Rectangle();

        headContent = new BorderPane();
        bodyContent = new HBox();

        try {
            super.setUnitLabel(this.rollershutterRemote.getData().getLabel());
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            super.setUnitLabel("UnknownID");
        }

        initTitle();
        initContent();
        createWidgetPane(headContent, bodyContent);

        try {
            initEffect();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }

        this.rollershutterRemote.addObserver(this);
    }

    private void effectGroup(final Double percentage) {
        rollerShutterIconBackground.setColor(Color.GRAY);
        rollerShutterIconForeground.setColor(Color.BLACK);

        clip.setWidth(Constants.SMALL_ICON);
        clip.setHeight(Constants.SMALL_ICON * percentage);
        rollerShutterIconForeground.setClip(clip);

        rollerShutterIcon.getChildren().addAll(rollerShutterIconBackground, rollerShutterIconForeground);
    }

    @Override
    protected void initTitle() {
        //effectGroup(0.9);

        headContent.setLeft(rollerShutterIcon);
        headContent.setCenter(new Label(super.getUnitLabel()));
        headContent.prefHeightProperty().set(Constants.SMALL_ICON + Constants.INSETS);
    }

    @Override
    protected void initContent() {
        final Button buttonUp = new Button();
        final Button buttonDown = new Button();
        final Button buttonOpen = new Button();
        final Button buttonClose = new Button();
        final SVGIcon arrowUp;
        final SVGIcon arrowDown;
        final SVGIcon arrowDoubleUP;
        final SVGIcon arrowDoubleDown;

        arrowUp = new SVGIcon(MaterialDesignIcon.CHEVRON_UP, Constants.SMALL_ICON, true);
        arrowDown = new SVGIcon(MaterialDesignIcon.CHEVRON_DOWN, Constants.SMALL_ICON, true);
        arrowDoubleUP = new SVGIcon(MaterialDesignIcon.CHEVRON_DOUBLE_UP, Constants.SMALL_ICON, true);
        arrowDoubleDown = new SVGIcon(MaterialDesignIcon.CHEVRON_DOUBLE_DOWN, Constants.SMALL_ICON, true);

        buttonUp.setGraphic(arrowUp);
        buttonDown.setGraphic(arrowDown);
        buttonOpen.setGraphic(arrowDoubleUP);
        buttonClose.setGraphic(arrowDoubleDown);

        final EventHandler<MouseEvent> sendingTotalOpening = event -> {
            try {
                rollershutterRemote.setOpeningRatio(0.0);
            } catch (CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            }
        };

        final EventHandler<MouseEvent> sendingTotalClosing = event -> {
            try {
                rollershutterRemote.setOpeningRatio(1.0);
            } catch (CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            }
        };

        final EventHandler<MouseEvent> sendingUpLive = event -> {
            try {
                if (rollershutterRemote.getOpeningRatio() >= Constants.ROLLERSHUTTER_STEP) {
                    rollershutterRemote.setOpeningRatio(rollershutterRemote.getOpeningRatio()
                            - Constants.ROLLERSHUTTER_STEP);
                }
                //rollershutterRemote.setShutter(ShutterStateType.ShutterState.State.UP);
            } catch (CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            }
        };

        final EventHandler<MouseEvent> sendingStop = event -> {
            try {
                rollershutterRemote.setShutter(ShutterStateType.ShutterState.State.STOP);
            } catch (CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            }
        };

        final EventHandler<MouseEvent> sendingDownLive = event -> {
            try {
                if (rollershutterRemote.getOpeningRatio() <= Constants.ROLLERSHUTTER_MAX_VALUE
                        - Constants.ROLLERSHUTTER_STEP) {
                    rollershutterRemote.setOpeningRatio(rollershutterRemote.getOpeningRatio()
                            + Constants.ROLLERSHUTTER_STEP);
                }
                //rollershutterRemote.setShutter(ShutterStateType.ShutterState.State.DOWN);
            } catch (CouldNotPerformException e) {
                ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            }
        };

        buttonOpen.setOnMouseClicked(sendingTotalOpening);
        buttonClose.setOnMouseClicked(sendingTotalClosing);
        buttonUp.setOnMouseClicked(sendingUpLive);
        buttonDown.setOnMouseClicked(sendingDownLive);
        //buttonUp.setOnMousePressed(sendingUpLive);
        //buttonUp.setOnMouseReleased(sendingStop);
        //buttonDown.setOnMousePressed(sendingDownLive);
        //buttonDown.setOnMouseReleased(sendingStop);

        bodyContent.getChildren().addAll(buttonUp, buttonDown, buttonOpen, buttonClose);
        //CHECKSTYLE.OFF: MagicNumber
        bodyContent.prefHeightProperty().set(100 + Constants.INSETS);
        //CHECKSTYLE.ON: MagicNumber
    }

    private void initEffect() throws CouldNotPerformException {
        final Double openingPercentage = rollershutterRemote.getOpeningRatio();
        effectGroup(openingPercentage);
    }

    @Override
    public DALRemoteService getDALRemoteService() {
        return rollershutterRemote;
    }

    @Override
    void removeObserver() {
        this.rollershutterRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object rollerShutter) throws java.lang.Exception {
        Platform.runLater(() -> {
            final Double openingPercentage = ((RollershutterType.Rollershutter) rollerShutter).getOpeningRatio();
            effectGroup(openingPercentage);
        });

    }
}
