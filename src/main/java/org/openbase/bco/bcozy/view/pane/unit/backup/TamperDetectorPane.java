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
package org.openbase.bco.bcozy.view.pane.unit.backup;

import org.openbase.bco.bcozy.view.pane.unit.AbstractUnitPane;
import org.openbase.bco.dal.remote.unit.TamperDetectorRemote;
import rst.domotic.unit.dal.TamperDetectorDataType.TamperDetectorData;

/**
 * Created by agatting on 11.04.16.
 */
public class TamperDetectorPane extends AbstractUnitPane<TamperDetectorRemote, TamperDetectorData> {

//    private static final Logger LOGGER = LoggerFactory.getLogger(TamperDetectorPane.class);
//    
//    private final TamperDetectorRemote tamperDetectorRemote;
//    private final SVGIcon tamperSwitchIconOk;
//    private final SVGIcon tamperSwitchIconManipulation;
//    private final SVGIcon unknownForegroundIcon;
//    private final SVGIcon unknownBackgroundIcon;
//    private final BorderPane headContent;
//
//    /**
//     * Constructor for the TamperSwitchPane.
//     *
//     * @param tamperSwitchRemote tamperSwitchRemote
//     */
    public TamperDetectorPane() {
        super(TamperDetectorRemote.class, false);
//        headContent = new BorderPane();
//        tamperSwitchIconOk = new SVGIcon(MaterialDesignIcon.CHECKBOX_MARKED_CIRCLE, Constants.SMALL_ICON, false);
//        tamperSwitchIconManipulation = new SVGIcon(MaterialDesignIcon.ALERT_CIRCLE, Constants.SMALL_ICON, false);
//        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
//        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);
//        
//        initUnitLabel();
//        initTitle();
//        initBodyContent();
//        createWidgetPane(headContent, false);
//        initEffect();
//        tooltip.textProperty().bind(labelText.textProperty());
//        
//        addObserverAndInitDisableState(this.tamperDetectorRemote);
    }
//    
//    private void initEffect() {
//        State tamperSwitchState = State.UNKNOWN;
//        
//        try {
//            tamperSwitchState = tamperDetectorRemote.getTamperState().getValue();
//        } catch (CouldNotPerformException e) {
//            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
//        }
//        setTamperSwitchIconAndText(tamperSwitchState);
//    }
//    
//    private void setTamperSwitchIconAndText(final State tamperSwitchState) {
//        iconPane.getChildren().clear();
//        
//        if (null != tamperSwitchState) {
//            switch (tamperSwitchState) {
//                case NO_TAMPER:
//                    iconPane.add(tamperSwitchIconOk, 0, 0);
//                    labelText.setIdentifier("noTamper");
//                    break;
//                case TAMPER:
//                    iconPane.add(tamperSwitchIconManipulation, 0, 0);
//                    labelText.setIdentifier("tamper");
//                    break;
//                default:
//                    iconPane.add(unknownBackgroundIcon, 0, 0);
//                    iconPane.add(unknownForegroundIcon, 0, 0);
//                    labelText.setIdentifier("unknown");
//                    break;
//            }
//        }
//    }
//    
//    @Override
//    protected void initTitle() {
//        unknownForegroundIcon.setForegroundIconColor(Color.BLUE);
//        unknownBackgroundIcon.setForegroundIconColor(Color.WHITE);
//        tamperSwitchIconOk.setForegroundIconColor(Color.GREEN);
//        tamperSwitchIconManipulation.setForegroundIconColor(Color.RED);
//        
//        headContent.setCenter(getUnitLabel());
//        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
//        headContent.prefHeightProperty().set(tamperSwitchIconOk.getSize() + Constants.INSETS);
//    }
//    
//    @Override
//    protected void initBodyContent() {
//        //No body content.
//    }
//    
//    @Override
//    public void update(final Observable observable, final Object tamperSwitch) throws java.lang.Exception {
//        Platform.runLater(() -> {
//            final State tamperSwitchState = ((TamperDetectorData) tamperSwitch).getTamperState().getValue();
//            
//            setTamperSwitchIconAndText(tamperSwitchState);
//        });
//    }
}
