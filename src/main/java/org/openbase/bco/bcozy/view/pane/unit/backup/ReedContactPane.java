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
import org.openbase.bco.dal.remote.unit.ReedContactRemote;
import rst.domotic.unit.dal.ReedContactDataType.ReedContactData;

/**
 * Created by agatting on 27.01.16.
 */
public class ReedContactPane extends AbstractUnitPane<ReedContactRemote, ReedContactData> {

//    private static final Logger LOGGER = LoggerFactory.getLogger(ReedContactPane.class);
//
//    private final ReedContactRemote reedContactRemote;
//    private final SVGIcon unknownForegroundIcon;
//    private final SVGIcon unknownBackgroundIcon;
//    private final SVGIcon reedSwitchIcon;
//    private final BorderPane headContent;
//
//    /**
//     * Constructor for the ReedSwitchPane.
//     *
//     * @param reedSwitchRemote reedSwitchRemote
//     */
    public ReedContactPane() {
        super(ReedContactRemote.class, false);
//        reedSwitchIcon = new SVGIcon(MaterialIcon.RADIO_BUTTON_CHECKED, Constants.SMALL_ICON, true);
//        unknownBackgroundIcon = new SVGIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE, Constants.SMALL_ICON - 2, false);
//        unknownForegroundIcon = new SVGIcon(MaterialDesignIcon.HELP_CIRCLE, Constants.SMALL_ICON, false);
//        headContent = new BorderPane();
//
//        initTitle();
//        initBodyContent();
//        createWidgetPane(headContent, false);
//        initEffectAndText();
//        tooltip.textProperty().bind(labelText.textProperty());
    }
//
//    private void initEffectAndText() {
//        State reedSwitchState = State.UNKNOWN;
//
//        try {
//            reedSwitchState = reedContactRemote.getContactState().getValue();
//        } catch (CouldNotPerformException e) {
//            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
//        }
//        setReedSwitchIconAndTooltip(reedSwitchState);
//    }
//
//    private void setReedSwitchIconAndTooltip(final State reedSwitchState) {
//        iconPane.getChildren().clear();
//
//        if (reedSwitchState == State.CLOSED) {
//            reedSwitchIcon.changeForegroundIcon(MaterialIcon.RADIO_BUTTON_CHECKED);
//            iconPane.add(reedSwitchIcon, 0, 0);
//            labelText.setIdentifier("closed");
//        } else if (reedSwitchState == State.OPEN) {
//            reedSwitchIcon.changeForegroundIcon(MaterialIcon.RADIO_BUTTON_UNCHECKED);
//            iconPane.add(reedSwitchIcon, 0, 0);
//            labelText.setIdentifier("open");
//        } else {
//            iconPane.add(unknownBackgroundIcon, 0, 0);
//            iconPane.add(unknownForegroundIcon, 0, 0);
//            labelText.setIdentifier("unknown");
//        }
//    }
//
//    @Override
//    protected void initTitle() {
//        unknownForegroundIcon.setForegroundIconColor(Color.BLUE);
//        unknownBackgroundIcon.setForegroundIconColor(Color.WHITE);
//
//        headContent.setCenter(getUnitLabel());
//        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
//        headContent.prefHeightProperty().set(reedSwitchIcon.getSize() + Constants.INSETS);
//    }
//
//    @Override
//    protected void initBodyContent() {
//        //No body content.
//    }
//
//    @Override
//    public void update(final Observable observable, final Object reedSwitch) throws java.lang.Exception {
//        Platform.runLater(() -> {
//            final State reedSwitchState = ((ReedContactData) reedSwitch).getContactState().getValue();
//
//            setReedSwitchIconAndTooltip(reedSwitchState);
//        });
//    }
}
