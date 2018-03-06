/**
 * ==================================================================
 *
 * This file is part of org.openbase.bco.bcozy.
 *
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3) as published
 * by the Free Software Foundation.
 *
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.view.pane.unit.backup;

import org.openbase.bco.bcozy.view.pane.unit.AbstractUnitPane;
import org.openbase.bco.dal.remote.unit.RollerShutterRemote;
import rst.domotic.unit.dal.RollerShutterDataType.RollerShutterData;

/**
 * Created by hoestreich on 11/19/15.
 */
public class RollerShutterPane extends AbstractUnitPane<RollerShutterRemote, RollerShutterData> {

//    private static final Logger LOGGER = LoggerFactory.getLogger(RollerShutterPane.class);
//
//    private final RollerShutterRemote rollershutterRemote;
//    private final BorderPane headContent;
//    private final HBox bodyContent;
//    private final SVGIcon rollerShutterIconBackground;
//    private final SVGIcon rollerShutterIconForeground;
//    private final SVGIcon unknownForegroundIcon;
//    private final SVGIcon unknownBackgroundIcon;
//    private final Rectangle clip;
//    private final Text rollerShutterStatus;
//
//    private final EventHandler<MouseEvent> sendingUp = event -> GlobalCachedExecutorService.submit(new Task() {
//        @Override
//        protected Object call() {
//            try {
//                rollershutterRemote.setBlindState(MovementState.UP).get(Constants.OPERATION_SERVICE_MILLI_TIMEOUT, TimeUnit.MILLISECONDS);
//            } catch (InterruptedException | ExecutionException | TimeoutException | CouldNotPerformException ex) {
//                ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
//            }
//            return null;
//        }
//    });
//
//    private final EventHandler<MouseEvent> sendingDown = event -> GlobalCachedExecutorService.submit(new Task() {
//        @Override
//        protected Object call() {
//            try {
//                rollershutterRemote.setBlindState(MovementState.DOWN).get(Constants.OPERATION_SERVICE_MILLI_TIMEOUT, TimeUnit.MILLISECONDS);
//            } catch (InterruptedException | ExecutionException | TimeoutException | CouldNotPerformException ex) {
//                ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
//            }
//            return null;
//        }
//    });
//
//    private final EventHandler<MouseEvent> sendingStop = event -> GlobalCachedExecutorService.submit(new Task() {
//        @Override
//        protected Object call() {
//            try {
//                rollershutterRemote.setBlindState(MovementState.STOP).get(Constants.OPERATION_SERVICE_MILLI_TIMEOUT, TimeUnit.MILLISECONDS);
//            } catch (InterruptedException | ExecutionException | TimeoutException | CouldNotPerformException ex) {
//                ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
//            }
//            return null;
//        }
//    });
//
//    private final EventHandler<MouseEvent> sendingTotalOpening = event -> GlobalCachedExecutorService.submit(new Task() {
//        @Override
//        protected Object call() {
//            try {
//                rollershutterRemote.setBlindState(BlindState.newBuilder().setOpeningRatio(0.0).setMovementState(MovementState.STOP).build()).get(Constants.OPERATION_SERVICE_MILLI_TIMEOUT, TimeUnit.MILLISECONDS);
//            } catch (InterruptedException | ExecutionException | TimeoutException | CouldNotPerformException ex) {
//                ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
//            }
//            return null;
//        }
//    });
//
//    private final EventHandler<MouseEvent> sendingTotalClosing = event -> GlobalCachedExecutorService.submit(new Task() {
//        @Override
//        protected Object call() {
//            try {
//                rollershutterRemote.setBlindState(BlindState.newBuilder().setOpeningRatio(1.0).setMovementState(MovementState.STOP).build()).get(Constants.OPERATION_SERVICE_MILLI_TIMEOUT, TimeUnit.MILLISECONDS);
//            } catch (InterruptedException | ExecutionException | TimeoutException | CouldNotPerformException ex) {
//                ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
//            }
//            return null;
//        }
//    });
//
//    /**
//     * Constructor for a RollerShutterPane.
//     *
//     * @param rollerShutterRemote UnitRemote
//     */
    public RollerShutterPane() {
        super(RollerShutterRemote.class, true);
//
//        rollerShutterIconBackground = new SVGIcon(MaterialDesignIcon.FORMAT_ALIGN_JUSTIFY, JFXConstants.ICON_SIZE_SMALL, false);
//        rollerShutterIconForeground = new SVGIcon(MaterialDesignIcon.FORMAT_ALIGN_JUSTIFY, JFXConstants.ICON_SIZE_SMALL, true);
//        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, JFXConstants.ICON_SIZE_SMALL - 2, false);
//        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, JFXConstants.ICON_SIZE_SMALL, false);
//        headContent = new BorderPane();
//        bodyContent = new HBox();
//        rollerShutterStatus = new Text();
//        clip = new Rectangle();
//
//        initTitle();
//        initBodyContent();
//        initWidgetPane(headContent, bodyContent, false);
//        initEffect();
//        tooltip.textProperty().bind(labelText.textProperty());
    }
//
//    private void initEffect() {
//        MovementState shutterState = MovementState.UNKNOWN;
//        double openingPercentage = 0.0;
//
//        try {
//            openingPercentage = rollershutterRemote.getBlindState().getOpeningRatio();
//            shutterState = rollershutterRemote.getBlindState().getMovementState();
//        } catch (CouldNotPerformException ex) {
//            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
//        }
//        setEffectOpeningRatio(openingPercentage, shutterState);
//    }
//
//    private void setEffectOpeningRatio(final double percentage, final MovementState shutterState) {
//        iconPane.getChildren().clear();
//
//        if (shutterState.equals(MovementState.UNKNOWN)) {
//            rollerShutterStatus.setText((int) (Constants.ONE_HUNDRED * percentage) + Constants.PERCENTAGE);
//
//            iconPane.add(unknownBackgroundIcon, 0, 0);
//            iconPane.add(unknownForegroundIcon, 0, 0);
//            labelText.setIdentifier("unknown");
//        } else {
//            switch (shutterState) {
//                case DOWN:
//                    labelText.setIdentifier("down");
//                    break;
//                case UP:
//                    labelText.setIdentifier("up");
//                    break;
//                default:
//                    labelText.setIdentifier("stop");
//                    break;
//            }
//            rollerShutterIconBackground.changeBackgroundIcon(MaterialDesignIcon.FORMAT_ALIGN_JUSTIFY);
//            rollerShutterIconBackground.setForegroundIconColor(Color.YELLOW);
//            rollerShutterIconForeground.changeForegroundIcon(MaterialDesignIcon.FORMAT_ALIGN_JUSTIFY);
//            ((Rectangle) rollerShutterIconForeground.getClip()).setHeight(JFXConstants.ICON_SIZE_SMALL * percentage);
//
//            rollerShutterStatus.setText((int) (Constants.ONE_HUNDRED * percentage) + Constants.PERCENTAGE);
//
//            iconPane.add(rollerShutterIconBackground, 0, 0);
//            iconPane.add(rollerShutterIconForeground, 0, 0);
//        }
//        iconPane.add(rollerShutterStatus, 1, 0);
//    }
//
//    @Override
//    protected void initTitle() {
//        rollerShutterStatus.getStyleClass().add(Constants.ICONS_CSS_STRING);
//        unknownForegroundIcon.setForegroundIconColor(Color.BLUE);
//        unknownBackgroundIcon.setForegroundIconColor(Color.WHITE);
//
//        iconPane.setHgap(Constants.INSETS);
//        headContent.setCenter(getUnitLabel());
//        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
//        headContent.prefHeightProperty().set(JFXConstants.ICON_SIZE_SMALL + Constants.INSETS);
//
//        clip.setWidth(JFXConstants.ICON_SIZE_SMALL);
//        clip.setHeight(JFXConstants.ICON_SIZE_SMALL);
//
//        rollerShutterIconForeground.setClip(clip);
//    }
//
//    @Override
//    protected void initBodyContent() { //NOPMD
//        final SVGIcon arrowUp;
//        final SVGIcon arrowDown;
//        final SVGIcon arrowDoubleUP;
//        final SVGIcon arrowDoubleDown;
//        final Button buttonUp = new Button();
//        final Button buttonDown = new Button();
//        final Button buttonOpen = new Button();
//        final Button buttonClose = new Button();
//
//        buttonOpen.setOnMouseClicked(sendingTotalOpening);
//        buttonClose.setOnMouseClicked(sendingTotalClosing);
//        buttonUp.setOnMousePressed(sendingUp);
//        buttonUp.setOnMouseReleased(sendingStop);
//        buttonDown.setOnMousePressed(sendingDown);
//        buttonDown.setOnMouseReleased(sendingStop);
//
//        arrowUp = new SVGIcon(MaterialDesignIcon.CHEVRON_UP, JFXConstants.ICON_SIZE_SMALL, true);
//        arrowDown = new SVGIcon(MaterialDesignIcon.CHEVRON_DOWN, JFXConstants.ICON_SIZE_SMALL, true);
//        arrowDoubleUP = new SVGIcon(MaterialDesignIcon.CHEVRON_DOUBLE_UP, JFXConstants.ICON_SIZE_SMALL, true);
//        arrowDoubleDown = new SVGIcon(MaterialDesignIcon.CHEVRON_DOUBLE_DOWN, JFXConstants.ICON_SIZE_SMALL, true);
//
//        buttonUp.setGraphic(arrowUp);
//        buttonDown.setGraphic(arrowDown);
//        buttonOpen.setGraphic(arrowDoubleUP);
//        buttonClose.setGraphic(arrowDoubleDown);
//
//        bodyContent.getChildren().addAll(buttonOpen, buttonUp, buttonDown, buttonClose);
//        bodyContent.setAlignment(Pos.CENTER);
//        //CHECKSTYLE.OFF: MagicNumber
//        bodyContent.prefHeightProperty().set(70 + Constants.INSETS);
//        //CHECKSTYLE.ON: MagicNumber
//    }
//
//    @Override
//    public void update(final Observable observable, final Object rollerShutter) throws java.lang.Exception {
//        Platform.runLater(() -> {
//            final double openingPercentage = ((RollerShutterData) rollerShutter).getBlindState().getOpeningRatio();
//            final MovementState shutterState = ((RollerShutterData) rollerShutter).getBlindState().getMovementState();
//            setEffectOpeningRatio(openingPercentage, shutterState);
//        });
//    }
}
