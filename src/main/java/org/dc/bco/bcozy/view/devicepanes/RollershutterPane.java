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

import de.citec.dal.remote.unit.DALRemoteService;
import de.citec.dal.remote.unit.RollershutterRemote;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.printer.ExceptionPrinter;
import de.citec.jul.exception.printer.LogLevel;
import de.citec.jul.pattern.Observable;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.controlsfx.control.PlusMinusSlider;
import org.dc.bco.bcozy.view.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hoestreich on 11/19/15.
 */
public class RollershutterPane extends UnitPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(RollershutterPane.class);

    private final RollershutterRemote rollershutterRemote;
    private final Image imageIcon;

    /**
     * Constructor for a RollershutterPane.
     * @param rollershutterRemote DALRemoteService
     */
    public RollershutterPane(final DALRemoteService rollershutterRemote) {
        this.rollershutterRemote = (RollershutterRemote) rollershutterRemote;
        imageIcon = new Image("/icons/shutter.png");

        try {
            super.setUnitLabel(this.rollershutterRemote.getData().getLabel());
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
            super.setUnitLabel("UnknownID");
        }

        initTitle();
        initContent();
    }

    @Override
    protected void initTitle() {
        final ImageView rollershutterIcon;
        final BorderPane borderPane;

        //image
        rollershutterIcon = new ImageView(imageIcon);
        //rollershutterIcon.setClip(new ImageView(icon));
        rollershutterIcon.setFitHeight(Constants.MIDDLE_ICON);
        rollershutterIcon.setFitWidth(Constants.MIDDLE_ICON);

        //borderPane for header of titledPane as "graphic"
        borderPane = new BorderPane();
        borderPane.setLeft(rollershutterIcon);
        borderPane.setCenter(new Label(super.getUnitLabel()));

        this.getStyleClass().add("widgetPane");
        this.setGraphic(borderPane);
    }

    @Override
    protected void initContent() {
        final BorderPane borderPane = new BorderPane();
        final BorderPane verticalLayout = new BorderPane();
        //TODO: Implement Icon class which adapts its size depending on the window size (responsive)
        final Label actualValue = new Label("Aktueller Wert: 15");
                                            // + Double.toString(shutterInstance.getOpeningRatio()));
        final ImageView imageViewIcon = new ImageView(imageIcon);
        imageViewIcon.setFitHeight(Constants.MIDDLE_ICON);
        imageViewIcon.setFitWidth(Constants.MIDDLE_ICON);

        final PlusMinusSlider control = new PlusMinusSlider();
        control.setOrientation(Orientation.VERTICAL);
        verticalLayout.setTop(imageViewIcon);
        verticalLayout.setBottom(actualValue);

        borderPane.setLeft(verticalLayout);
        borderPane.setRight(control);
        this.setContent(borderPane);
    }

    @Override
    void removeObserver() {
        //TODO: remove the observer
    }

    @Override
    public DALRemoteService getDALRemoteService() {
        return rollershutterRemote;
    }

    @Override
    public void update(final Observable observable, final Object rollerShutter) throws java.lang.Exception {
        //TODO: Do something
        //((RollershutterType.Rollershutter) rollerShutter).getLabel();

    }
}
