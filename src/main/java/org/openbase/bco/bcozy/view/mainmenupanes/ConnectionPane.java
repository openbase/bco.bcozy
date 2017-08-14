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
package org.openbase.bco.bcozy.view.mainmenupanes;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.openbase.bco.bcozy.view.AnimationProvider;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.SVGIcon;

/**
 * Created by hoestreich on 12/8/15.
 */
public class ConnectionPane extends VBox {

    private final SVGIcon connectionSuccessView;
    private final SVGIcon connectionProblemView;
    private final SVGIcon connectionUploadView;
    private final SVGIcon connectionDownloadView;
    private final SVGIcon connectionSuccessViewSmall;
    private final SVGIcon connectionProblemViewSmall;
    private final SVGIcon connectionUploadViewSmall;
    private final SVGIcon connectionDownloadViewSmall;
    private boolean test;
    private final FadeTransition problemFade, problemFadeSmall;
    private final GridPane connectionIcon, connectionIconSmall;

    /**
     * Constructor for the ConnectionPane.
     */
    public ConnectionPane() {
        connectionIcon = new GridPane();
        connectionIconSmall = new GridPane();
        final SVGIcon connectionView = new SVGIcon(FontAwesomeIcon.DATABASE, Constants.LOGO_ICON, true);
        final SVGIcon connectionViewSmall = new SVGIcon(FontAwesomeIcon.DATABASE, Constants.SMALL_ICON, true);

        connectionSuccessView = new SVGIcon(FontAwesomeIcon.CHECK_CIRCLE, Constants.EXTRA_SMALL_ICON, true);
        connectionSuccessView.setOpacity(Constants.FULLY_TRANSPARENT);
        connectionSuccessViewSmall = new SVGIcon(FontAwesomeIcon.CHECK_CIRCLE, Constants.EXTRA_SMALL_ICON, true);
        connectionSuccessViewSmall.setOpacity(Constants.FULLY_TRANSPARENT);

        connectionProblemView = new SVGIcon(FontAwesomeIcon.QUESTION_CIRCLE, Constants.EXTRA_SMALL_ICON, true);
        connectionProblemView.setOpacity(Constants.FULLY_TRANSPARENT);
        connectionProblemViewSmall = new SVGIcon(FontAwesomeIcon.QUESTION_CIRCLE, Constants.EXTRA_SMALL_ICON, true);
        connectionProblemViewSmall.setOpacity(Constants.FULLY_TRANSPARENT);

        connectionUploadView = new SVGIcon(FontAwesomeIcon.ARROW_UP, Constants.EXTRA_EXTRA_SMALL_ICON, true);
        connectionUploadView.setOpacity(Constants.FULLY_TRANSPARENT);
        connectionUploadViewSmall = new SVGIcon(FontAwesomeIcon.ARROW_UP, Constants.EXTRA_SMALL_ICON, true);
        connectionUploadViewSmall.setOpacity(Constants.FULLY_TRANSPARENT);

        connectionDownloadView = new SVGIcon(FontAwesomeIcon.ARROW_DOWN, Constants.EXTRA_EXTRA_SMALL_ICON, true);
        connectionDownloadView.setOpacity(Constants.FULLY_TRANSPARENT);
        connectionDownloadViewSmall = new SVGIcon(FontAwesomeIcon.ARROW_DOWN, Constants.EXTRA_SMALL_ICON, true);
        connectionDownloadViewSmall.setOpacity(Constants.FULLY_TRANSPARENT);

        test = false;
        connectionView.setOnMouseClicked(event -> testAnimations());
        connectionIcon.add(connectionView, 0, 0, 1, 2);
        connectionIcon.add(connectionSuccessView, 1, 0);
        connectionIcon.add(connectionProblemView, 1, 0);
        connectionIcon.add(connectionUploadView, 1, 1);
        connectionIcon.add(connectionDownloadView, 1, 1);
        connectionIcon.setHgap(2);
        connectionIcon.setAlignment(Pos.CENTER);

        connectionViewSmall.setOnMouseClicked(event -> testAnimationsSmall());
        connectionIconSmall.add(connectionViewSmall, 0, 0, 1, 2);
        connectionIconSmall.add(connectionSuccessViewSmall, 1, 0);
        connectionIconSmall.add(connectionProblemViewSmall, 1, 0);
        connectionIconSmall.add(connectionUploadViewSmall, 1, 1);
        connectionIconSmall.add(connectionDownloadViewSmall, 1, 1);
        connectionIconSmall.setHgap(2);
        connectionIconSmall.setAlignment(Pos.CENTER);

        this.getChildren().addAll(connectionIcon);

        problemFade = AnimationProvider.createFadeTransition(
                connectionProblemView, Constants.NO_TRANSPARENCY, Constants.NEARLY_TRANSPARENT,
                Animation.INDEFINITE, Constants.GLOWING_FADE_DURATION);

        problemFadeSmall = AnimationProvider.createFadeTransition(
                connectionProblemViewSmall, Constants.NO_TRANSPARENCY, Constants.NEARLY_TRANSPARENT,
                Animation.INDEFINITE, Constants.GLOWING_FADE_DURATION);
    }

    private void testAnimations() {
        if (test) {
            connectionEstablished(connectionSuccessView, connectionProblemView, problemFade);
            uploadActive(connectionUploadView);
            test = false;
        } else {
            connectionProblem(connectionSuccessView, connectionProblemView, problemFade);
            downloadActive(connectionDownloadView);
            test = true;
        }
    }

    private void testAnimationsSmall() {
        if (test) {
            connectionEstablished(connectionSuccessViewSmall, connectionProblemViewSmall, problemFadeSmall);
            uploadActive(connectionUploadViewSmall);
            test = false;
        } else {
            connectionProblem(connectionSuccessViewSmall, connectionProblemViewSmall, problemFadeSmall);
            downloadActive(connectionDownloadViewSmall);
            test = true;
        }
    }
    /**
     * Show the tick mark to indicate that the connection is established and no problems are detected.
     */
    public void connectionEstablished(SVGIcon successView, SVGIcon problemView,  FadeTransition transition) {
        successView.setOpacity(Constants.NO_TRANSPARENCY);
        successView.setForegroundIconColorAnimated(Color.LIMEGREEN, 1);
        transition.stop();
        problemView.setOpacity(Constants.FULLY_TRANSPARENT);
    }

    /**
     * Show the question mark to indicate that the connection has problems.
     * Animation should generate attention.
     */
    public void connectionProblem(SVGIcon successView, SVGIcon problemView,  FadeTransition transition) {
        successView.setOpacity(Constants.FULLY_TRANSPARENT);
        problemView.setForegroundIconColorAnimated(Color.TOMATO, Animation.INDEFINITE);
        transition.play();
    }

    /**
     * Show upload arrow to indication that a server request was made.
     */
    public void uploadActive(SVGIcon uploadView) {
        uploadView.setOpacity(Constants.NO_TRANSPARENCY);
        final FadeTransition uploadFade = AnimationProvider.createFadeTransition(
                uploadView, Constants.FULLY_TRANSPARENT,
                Constants.NO_TRANSPARENCY, 1, Constants.FASTFADEDURATION);
        uploadFade.play();
        uploadFade.setOnFinished(event -> uploadView.setOpacity(Constants.FULLY_TRANSPARENT));
    }

    /**
     * Show download arrow to indicate that data is received.
     */
    public void downloadActive(SVGIcon downloadView) {
        downloadView.setOpacity(Constants.NO_TRANSPARENCY);
        final FadeTransition downloadFade = AnimationProvider.createFadeTransition(
                downloadView, Constants.FULLY_TRANSPARENT,
                Constants.NO_TRANSPARENCY, 1, Constants.FASTFADEDURATION);
        downloadFade.play();
        downloadFade.setOnFinished(event -> downloadView.setOpacity(Constants.FULLY_TRANSPARENT));
    }

    /**
     * Necessary method which adds the connectionIcon (GridPane) again to the PaneElement.
     * (Is taken away when the mainMenu is minimzed)
     */
    public void maximize() {
        if (connectionIcon != null) {
           // this.getChildren().addAll(connectionIcon);
        }
    }

    //@Override
    public Node getStatusIcon() {
        return connectionIconSmall;
    }

}
