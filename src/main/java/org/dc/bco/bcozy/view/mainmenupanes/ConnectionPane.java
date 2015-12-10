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

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.dc.bco.bcozy.view.Constants;

/**
 * Created by hoestreich on 12/8/15.
 */
public class ConnectionPane extends VBox {

    private final ImageView connectionSuccessView;
    private final ImageView connectionProblemView;
    private final ImageView connectionUploadView;
    private final ImageView connectionDownloadView;
    private boolean test;
    private final FadeTransition problemFade;

    /**
     * Constructor for the ConnectionPane.
     */
    public ConnectionPane() {
        final StackPane connectionIcon = new StackPane();
        final Image connectionImage = new Image(getClass().getResourceAsStream("/icons/connection.png"),
                Constants.MIDDLE_ICON, Constants.MIDDLE_ICON, true, true);
        final ImageView connectionView = new ImageView(connectionImage);

        final Image connectionSuccessImage =
                new Image(getClass().getResourceAsStream("/icons/connection-successful.png"), Constants.MIDDLE_ICON,
                        Constants.MIDDLE_ICON, true, true);
        connectionSuccessView = new ImageView(connectionSuccessImage);
        connectionSuccessView.setOpacity(Constants.FULLYTRANSPARENT);

        final Image connectionProblemImage = new Image(getClass().getResourceAsStream("/icons/connection-problem.png"),
                Constants.MIDDLE_ICON, Constants.MIDDLE_ICON, true, true);
        connectionProblemView = new ImageView(connectionProblemImage);
        connectionProblemView.setOpacity(Constants.FULLYTRANSPARENT);

        final Image connectionUploadImage = new Image(getClass().getResourceAsStream("/icons/connection-upload.png"),
                Constants.MIDDLE_ICON, Constants.MIDDLE_ICON, true, true);
        connectionUploadView = new ImageView(connectionUploadImage);
        connectionUploadView.setOpacity(Constants.FULLYTRANSPARENT);

        final Image connectionDownloadImage =
                new Image(getClass().getResourceAsStream("/icons/connection-download.png"), Constants.MIDDLE_ICON,
                        Constants.MIDDLE_ICON, true, true);
        connectionDownloadView = new ImageView(connectionDownloadImage);
        connectionDownloadView.setOpacity(Constants.FULLYTRANSPARENT);

        test = false;
        connectionView.setOnMouseClicked(event -> testAnimations());
        connectionIcon.getChildren().addAll(connectionView, connectionSuccessView, connectionProblemView,
                connectionUploadView, connectionDownloadView);

        this.getChildren().addAll(connectionIcon);

        problemFade = createFadeTransition(connectionProblemView, Constants.NOTRANSPARENCY, Constants.NEARLYTRANSPARENT,
                Animation.INDEFINITE, Constants.GLOWINGFADEDURATION);
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
        connectionSuccessView.setOpacity(Constants.NOTRANSPARENCY);
        problemFade.stop();
        connectionProblemView.setOpacity(Constants.FULLYTRANSPARENT);
    }

    /**
     * Show the question mark to indicate that the connection has problems.
     * Animation should generate attention.
     */
    public void connectionProblem() {
        connectionSuccessView.setOpacity(Constants.FULLYTRANSPARENT);
        problemFade.play();
    }

    /**
     * Show upload arrow to indication that a server request was made.
     */
    public void uploadActive() {
        connectionUploadView.setOpacity(Constants.NOTRANSPARENCY);
        final FadeTransition uploadFade = createFadeTransition(connectionUploadView, Constants.FULLYTRANSPARENT,
                Constants.NOTRANSPARENCY, 1, Constants.FASTFADEDURATION);
        uploadFade.play();
        uploadFade.setOnFinished(event -> connectionUploadView.setOpacity(Constants.FULLYTRANSPARENT));
    }

    /**
     * Show download arrow to indicate that data is received.
     */
    public void downloadActive() {
        connectionDownloadView.setOpacity(Constants.NOTRANSPARENCY);
        final FadeTransition downloadFade = createFadeTransition(connectionDownloadView, Constants.FULLYTRANSPARENT,
                Constants.NOTRANSPARENCY, 1, Constants.FASTFADEDURATION);
        downloadFade.play();
        downloadFade.setOnFinished(event -> connectionDownloadView.setOpacity(Constants.FULLYTRANSPARENT));
    }

    private FadeTransition createFadeTransition(final Node node, final double fromValue, final double toValue,
                                                final int cycleCount, final double duration) {
        final FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), node);
        fadeTransition.setFromValue(fromValue);
        fadeTransition.setToValue(toValue);
        fadeTransition.setCycleCount(cycleCount);
        fadeTransition.setAutoReverse(true);
        return fadeTransition;
    }
}
