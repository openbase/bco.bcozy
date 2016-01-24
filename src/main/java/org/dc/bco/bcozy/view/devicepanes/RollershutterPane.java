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
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
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
    private final Rectangle clip;
    private final Text rollerShutterStatus;
    private final GridPane iconPane;

    /**
     * Constructor for a RollerShutterPane.
     * @param rollerShutterRemote DALRemoteService
     */
    public RollershutterPane(final DALRemoteService rollerShutterRemote) {
        this.rollershutterRemote = (RollershutterRemote) rollerShutterRemote;

        rollerShutterIconBackground = new SVGIcon(MaterialDesignIcon.FORMAT_ALIGN_JUSTIFY, Constants.SMALL_ICON, false);
        rollerShutterIconForeground = new SVGIcon(MaterialDesignIcon.FORMAT_ALIGN_JUSTIFY, Constants.SMALL_ICON, true);
        clip = new Rectangle();
        rollerShutterStatus = new Text();
        iconPane = new GridPane();

        headContent = new BorderPane();
        bodyContent = new HBox();

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, bodyContent);
        initEffect();

        this.rollershutterRemote.addObserver(this);
    }

    private void setEffectOpeningRatio(final double percentage) {
        ((Rectangle) rollerShutterIconForeground.getClip()).setHeight(Constants.SMALL_ICON * percentage);
        rollerShutterStatus.setText((int) (Constants.ONE_HUNDRED * percentage) + "%");
    }

    private void initEffect() {
        double openingPercentage = 0.0;
        try {
            openingPercentage = rollershutterRemote.getOpeningRatio();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }

        setEffectOpeningRatio(openingPercentage);
    }

    @Override
    protected void initTitle() {
        rollerShutterStatus.getStyleClass().add("icons");

        iconPane.add(rollerShutterIconBackground, 0, 0);
        iconPane.add(rollerShutterIconForeground, 0, 0);
        iconPane.add(rollerShutterStatus, 1, 0);

        headContent.setLeft(iconPane);
        headContent.setCenter(new Label(super.getUnitLabel()));
        headContent.prefHeightProperty().set(Constants.SMALL_ICON + Constants.INSETS);

        clip.setWidth(Constants.SMALL_ICON);
        clip.setHeight(Constants.SMALL_ICON);

        rollerShutterIconBackground.setColor(Color.YELLOW);
        rollerShutterIconForeground.setClip(clip);
    }

    @Override
    protected void initContent() { //NOPMD
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

        final EventHandler<MouseEvent> sendingTotalOpening = event -> new Thread(new Task() {
            @Override
            protected Object call() {
                try {
                    rollershutterRemote.setOpeningRatio(0.0);
                } catch (CouldNotPerformException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                }
                return null;
            }
        }).start();

        final EventHandler<MouseEvent> sendingTotalClosing = event -> new Thread(new Task() {
            @Override
            protected Object call() {
                try {
                    rollershutterRemote.setOpeningRatio(1.0);
                } catch (CouldNotPerformException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                }
                return null;
            }
        }).start();

        final EventHandler<MouseEvent> sendingUpLive = event -> new Thread(new Task() {
            @Override
            protected Object call() {
                try {
                    if (rollershutterRemote.getOpeningRatio() >= Constants.ROLLERSHUTTER_STEP) {
                        rollershutterRemote.setOpeningRatio(rollershutterRemote.getOpeningRatio()
                                - Constants.ROLLERSHUTTER_STEP);
                    }

                    rollershutterRemote.setShutter(ShutterStateType.ShutterState.State.UP);

                } catch (CouldNotPerformException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                }
                return null;
            }
        }).start();

        /*final EventHandler<MouseEvent> sendingStop = event -> new Thread(new Task() {
            @Override
            protected Object call() {
                try {
                    rollershutterRemote.setShutter(ShutterStateType.ShutterState.State.STOP);
                } catch (CouldNotPerformException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                }
                return null;
            }
        }).start();*/

        final EventHandler<MouseEvent> sendingDownLive = event -> new Thread(new Task() {
            @Override
            protected Object call() {
                try {
                    if (rollershutterRemote.getOpeningRatio() <= Constants.ROLLERSHUTTER_MAX_VALUE) {
                        rollershutterRemote.setOpeningRatio(rollershutterRemote.getOpeningRatio()
                                + Constants.ROLLERSHUTTER_STEP);
                    }
                    //rollershutterRemote.setShutter(ShutterStateType.ShutterState.State.DOWN);
                } catch (CouldNotPerformException e) {
                    ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
                }
                return null;
            }
        }).start();

        buttonOpen.setOnMouseClicked(sendingTotalOpening);
        buttonClose.setOnMouseClicked(sendingTotalClosing);
        buttonUp.setOnMouseClicked(sendingUpLive);
        buttonDown.setOnMouseClicked(sendingDownLive);
        //buttonUp.setOnMousePressed(sendingUpLive);
        //buttonUp.setOnMouseReleased(sendingStop);
        //buttonDown.setOnMousePressed(sendingDownLive);
        //buttonDown.setOnMouseReleased(sendingStop);

        bodyContent.getChildren().addAll(buttonOpen, buttonUp, buttonDown, buttonClose);
        bodyContent.setAlignment(Pos.CENTER);
        //CHECKSTYLE.OFF: MagicNumber
        bodyContent.prefHeightProperty().set(100 + Constants.INSETS);
        //CHECKSTYLE.ON: MagicNumber
    }

    @Override
    protected void initUnitLabel() {
        String unitLabel = Constants.UNKNOWN_ID;
        try {
            unitLabel = this.rollershutterRemote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabel(unitLabel);
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
            final double openingPercentage = ((RollershutterType.Rollershutter) rollerShutter).getOpeningRatio();
            setEffectOpeningRatio(openingPercentage);
        });

    }
}
