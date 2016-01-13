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
package org.dc.bco.bcozy.view.mainmenupanes;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import org.dc.bco.bcozy.view.AnimationProvider;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.SVGIcon;

/**
 * Created by hoestreich on 12/8/15.
 */
public class ConnectionPane extends PaneElement {

    private final SVGIcon connectionSuccessView;
    private final SVGIcon connectionProblemView;
    private final SVGIcon connectionUploadView;
    private final SVGIcon connectionDownloadView;
    private boolean test;
    private final FadeTransition problemFade;
    private final GridPane connectionIcon;

    /**
     * Constructor for the ConnectionPane.
     */
    public ConnectionPane() {
        connectionIcon = new GridPane();
        final SVGIcon connectionView = new SVGIcon(FontAwesomeIcon.DATABASE, Constants.SMALL_ICON, true);

        connectionSuccessView = new SVGIcon(FontAwesomeIcon.CHECK_CIRCLE, Constants.EXTRA_SMALL_ICON, true);
        connectionSuccessView.setOpacity(Constants.FULLY_TRANSPARENT);

        connectionProblemView = new SVGIcon(FontAwesomeIcon.QUESTION_CIRCLE, Constants.EXTRA_SMALL_ICON, true);
        connectionProblemView.setOpacity(Constants.FULLY_TRANSPARENT);

        connectionUploadView = new SVGIcon(FontAwesomeIcon.ARROW_UP, Constants.EXTRA_EXTRA_SMALL_ICON, true);
        connectionUploadView.setOpacity(Constants.FULLY_TRANSPARENT);

        connectionDownloadView = new SVGIcon(FontAwesomeIcon.ARROW_DOWN, Constants.EXTRA_EXTRA_SMALL_ICON, true);
        connectionDownloadView.setOpacity(Constants.FULLY_TRANSPARENT);

        test = false;
        connectionView.setOnMouseClicked(event -> testAnimations());
        connectionIcon.add(connectionView, 0, 0, 1, 2);
        connectionIcon.add(connectionSuccessView, 1, 0);
        connectionIcon.add(connectionProblemView, 1, 0);
        connectionIcon.add(connectionUploadView, 1, 1);
        connectionIcon.add(connectionDownloadView, 1, 1);
        connectionIcon.setHgap(2);
        connectionIcon.setAlignment(Pos.CENTER);

        this.getChildren().addAll(connectionIcon);

        problemFade = AnimationProvider.createFadeTransition(
                connectionProblemView, Constants.NO_TRANSPARENCY, Constants.NEARLY_TRANSPARENT,
                Animation.INDEFINITE, Constants.GLOWING_FADE_DURATION);
    }

    private void testAnimations() {
        if (test) {
            connectionEstablished();
            uploadActive();
            test = false;
        } else {
            connectionProblem();
            downloadActive();
            test = true;
        }
    }
    /**
     * Show the tick mark to indicate that the connection is established and no problems are detected.
     */
    public void connectionEstablished() {
        connectionSuccessView.setOpacity(Constants.NO_TRANSPARENCY);
        connectionSuccessView.setForegroundIconColorAnimated(Color.LIMEGREEN);
        problemFade.stop();
        connectionProblemView.setOpacity(Constants.FULLY_TRANSPARENT);
    }

    /**
     * Show the question mark to indicate that the connection has problems.
     * Animation should generate attention.
     */
    public void connectionProblem() {
        connectionSuccessView.setOpacity(Constants.FULLY_TRANSPARENT);
        connectionProblemView.setForegroundIconColorAnimated(Color.TOMATO);
        problemFade.play();
    }

    /**
     * Show upload arrow to indication that a server request was made.
     */
    public void uploadActive() {
        connectionUploadView.setOpacity(Constants.NO_TRANSPARENCY);
        final FadeTransition uploadFade = AnimationProvider.createFadeTransition(
                connectionUploadView, Constants.FULLY_TRANSPARENT,
                Constants.NO_TRANSPARENCY, 1, Constants.FASTFADEDURATION);
        uploadFade.play();
        uploadFade.setOnFinished(event -> connectionUploadView.setOpacity(Constants.FULLY_TRANSPARENT));
    }

    /**
     * Show download arrow to indicate that data is received.
     */
    public void downloadActive() {
        connectionDownloadView.setOpacity(Constants.NO_TRANSPARENCY);
        final FadeTransition downloadFade = AnimationProvider.createFadeTransition(
                connectionDownloadView, Constants.FULLY_TRANSPARENT,
                Constants.NO_TRANSPARENCY, 1, Constants.FASTFADEDURATION);
        downloadFade.play();
        downloadFade.setOnFinished(event -> connectionDownloadView.setOpacity(Constants.FULLY_TRANSPARENT));
    }

    /**
     * Necessary method which adds the connectionIcon (GridPane) again to the PaneElement.
     * (Is taken away when the mainMenu is minimzed)
     */
    public void maximize() {
        if (connectionIcon != null) {
            this.getChildren().addAll(connectionIcon);
        }
    }

    @Override
    public Node getStatusIcon() {
        return connectionIcon;
    }

}
