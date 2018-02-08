package org.openbase.bco.bcozy.view.mainmenupanes;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ImageViewProvider;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the Application Logo.
 *
 * @author vdasilva
 */
public class LogoPane extends HBox {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogoPane.class);

    private final ImageView logoView;
    private final ImageView logoViewSmall;

    public LogoPane() {
        logoView = ImageViewProvider.createImageView("/icons/bcozy_logo_simple_small.png", Constants.MAXLOGOWIDTH, Double.MAX_VALUE);
        logoViewSmall = ImageViewProvider.createImageView("/icons/bco_logo_simple_small.png", Constants.MIDDLE_ICON);

        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(logoView);
        this.setSpacing(10);
    }

    //@Override
    public Node getStatusIcon() {
        VBox statusIcons = new VBox(logoViewSmall);
        statusIcons.setAlignment(Pos.CENTER);
        statusIcons.setSpacing(20.0);
        return statusIcons;
    }
}
