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
import org.openbase.jul.visual.javafx.animation.Animations;
import org.openbase.jul.visual.javafx.JFXConstants;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;

/**
 * Created by hoestreich on 12/8/15.
 */
@Deprecated
public class ConnectionPane extends VBox {

    private final SVGGlyphIcon connectionSuccessView;
    private final SVGGlyphIcon connectionProblemView;
    private final SVGGlyphIcon connectionUploadView;
    private final SVGGlyphIcon connectionDownloadView;
    private final SVGGlyphIcon connectionSuccessViewSmall;
    private final SVGGlyphIcon connectionProblemViewSmall;
    private final SVGGlyphIcon connectionUploadViewSmall;
    private final SVGGlyphIcon connectionDownloadViewSmall;
    private boolean test;
    private final FadeTransition problemFade, problemFadeSmall;
    private final GridPane connectionIcon, connectionIconSmall;

    /**
     * Constructor for the ConnectionPane.
     */
    public ConnectionPane() {
        connectionIcon = new GridPane();
        connectionIconSmall = new GridPane();
        final SVGGlyphIcon connectionView = new SVGGlyphIcon(FontAwesomeIcon.DATABASE, JFXConstants.ICON_SIZE_MIDDLE, true);
        final SVGGlyphIcon connectionViewSmall = new SVGGlyphIcon(FontAwesomeIcon.DATABASE, JFXConstants.ICON_SIZE_SMALL, true);

        connectionSuccessView = new SVGGlyphIcon(FontAwesomeIcon.CHECK_CIRCLE, JFXConstants.ICON_SIZE_EXTRA_EXTRA_SMALL, true);
        connectionSuccessView.setOpacity(JFXConstants.TRANSPARENCY_FULLY);
        connectionSuccessViewSmall = new SVGGlyphIcon(FontAwesomeIcon.CHECK_CIRCLE, JFXConstants.ICON_SIZE_EXTRA_EXTRA_SMALL, true);
        connectionSuccessViewSmall.setOpacity(JFXConstants.TRANSPARENCY_FULLY);

        connectionProblemView = new SVGGlyphIcon(FontAwesomeIcon.QUESTION_CIRCLE, JFXConstants.ICON_SIZE_EXTRA_EXTRA_SMALL, true);
        connectionProblemView.setOpacity(JFXConstants.TRANSPARENCY_FULLY);
        connectionProblemViewSmall = new SVGGlyphIcon(FontAwesomeIcon.QUESTION_CIRCLE, JFXConstants.ICON_SIZE_EXTRA_EXTRA_SMALL, true);
        connectionProblemViewSmall.setOpacity(JFXConstants.TRANSPARENCY_FULLY);

        connectionUploadView = new SVGGlyphIcon(FontAwesomeIcon.ARROW_UP, JFXConstants.ICON_SIZE_EXTRA_EXTRA_SMALL, true);
        connectionUploadView.setOpacity(JFXConstants.TRANSPARENCY_FULLY);
        connectionUploadViewSmall = new SVGGlyphIcon(FontAwesomeIcon.ARROW_UP, JFXConstants.ICON_SIZE_EXTRA_EXTRA_SMALL, true);
        connectionUploadViewSmall.setOpacity(JFXConstants.TRANSPARENCY_FULLY);

        connectionDownloadView = new SVGGlyphIcon(FontAwesomeIcon.ARROW_DOWN, JFXConstants.ICON_SIZE_EXTRA_EXTRA_SMALL, true);
        connectionDownloadView.setOpacity(JFXConstants.TRANSPARENCY_FULLY);
        connectionDownloadViewSmall = new SVGGlyphIcon(FontAwesomeIcon.ARROW_DOWN, JFXConstants.ICON_SIZE_EXTRA_EXTRA_SMALL, true);
        connectionDownloadViewSmall.setOpacity(JFXConstants.TRANSPARENCY_FULLY);

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

        problemFade = Animations.createFadeTransition(
                connectionProblemView, JFXConstants.TRANSPARENCY_NONE, JFXConstants.TRANSPARENCY_NEARLY,
                Animation.INDEFINITE, JFXConstants.ANIMATION_DURATION_FADE_SLOW);

        problemFadeSmall = Animations.createFadeTransition(
                connectionProblemViewSmall, JFXConstants.TRANSPARENCY_NONE, JFXConstants.TRANSPARENCY_NEARLY,
                Animation.INDEFINITE, JFXConstants.ANIMATION_DURATION_FADE_GLOWING);
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
    public void connectionEstablished(SVGGlyphIcon successView, SVGGlyphIcon problemView,  FadeTransition transition) {
        successView.setOpacity(JFXConstants.TRANSPARENCY_NONE);
        successView.setForegroundIconColorAnimated(Color.LIMEGREEN, 1);
        transition.stop();
        problemView.setOpacity(JFXConstants.TRANSPARENCY_FULLY);
    }

    /**
     * Show the question mark to indicate that the connection has problems.
     * Animation should generate attention.
     */
    public void connectionProblem(SVGGlyphIcon successView, SVGGlyphIcon problemView,  FadeTransition transition) {
        successView.setOpacity(JFXConstants.TRANSPARENCY_FULLY);
        problemView.setForegroundIconColorAnimated(Color.TOMATO, Animation.INDEFINITE);
        transition.play();
    }

    /**
     * Show upload arrow to indication that a server request was made.
     */
    public void uploadActive(SVGGlyphIcon uploadView) {
        uploadView.setOpacity(JFXConstants.TRANSPARENCY_NONE);
        final FadeTransition uploadFade = Animations.createFadeTransition(
                uploadView, JFXConstants.TRANSPARENCY_FULLY,
                JFXConstants.TRANSPARENCY_NONE, 1, JFXConstants.ANIMATION_DURATION_FADE_FAST);
        uploadFade.play();
        uploadFade.setOnFinished(event -> uploadView.setOpacity(JFXConstants.TRANSPARENCY_FULLY));
    }

    /**
     * Show download arrow to indicate that data is received.
     */
    public void downloadActive(SVGGlyphIcon downloadView) {
        downloadView.setOpacity(JFXConstants.TRANSPARENCY_NONE);
        final FadeTransition downloadFade = Animations.createFadeTransition(
                downloadView, JFXConstants.TRANSPARENCY_FULLY,
                JFXConstants.TRANSPARENCY_NONE, 1, JFXConstants.ANIMATION_DURATION_FADE_FAST);
        downloadFade.play();
        downloadFade.setOnFinished(event -> downloadView.setOpacity(JFXConstants.TRANSPARENCY_FULLY));
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
