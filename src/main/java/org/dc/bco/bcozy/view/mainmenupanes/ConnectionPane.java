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
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.dc.bco.bcozy.view.AnimationProvider;
import org.dc.bco.bcozy.view.Constants;
import org.dc.bco.bcozy.view.ImageViewProvider;

/**
 * Created by hoestreich on 12/8/15.
 */
public class ConnectionPane extends PaneElement {

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
        final ImageView connectionView = ImageViewProvider
                .createImageView("/icons/connection.png", Constants.MIDDLE_ICON);

        connectionSuccessView = ImageViewProvider
                .createImageView("/icons/connection-successful.png", Constants.MIDDLE_ICON);
        connectionSuccessView.setOpacity(Constants.FULLY_TRANSPARENT);

        connectionProblemView = ImageViewProvider
                .createImageView("/icons/connection-problem.png", Constants.MIDDLE_ICON);
        connectionProblemView.setOpacity(Constants.FULLY_TRANSPARENT);

        connectionUploadView = ImageViewProvider
                .createImageView("/icons/connection-upload.png", Constants.MIDDLE_ICON);
        connectionUploadView.setOpacity(Constants.FULLY_TRANSPARENT);

        connectionDownloadView = ImageViewProvider
                .createImageView("/icons/connection-download.png", Constants.MIDDLE_ICON);
        connectionDownloadView.setOpacity(Constants.FULLY_TRANSPARENT);

        test = false;
        connectionView.setOnMouseClicked(event -> testAnimations());
        connectionIcon.getChildren().addAll(connectionView, connectionSuccessView, connectionProblemView,
                connectionUploadView, connectionDownloadView);

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
        problemFade.stop();
        connectionProblemView.setOpacity(Constants.FULLY_TRANSPARENT);
    }

    /**
     * Show the question mark to indicate that the connection has problems.
     * Animation should generate attention.
     */
    public void connectionProblem() {
        connectionSuccessView.setOpacity(Constants.FULLY_TRANSPARENT);
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

}
