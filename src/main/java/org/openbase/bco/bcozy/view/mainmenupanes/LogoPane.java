package org.openbase.bco.bcozy.view.mainmenupanes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.ImageViewProvider;
import org.openbase.bco.bcozy.view.MainMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Contains Application Logo and connection indicator.
 * @author vdasilva
 */
public class LogoPane extends HBox{
    private static final Logger LOGGER = LoggerFactory.getLogger(LogoPane.class);

    private final ConnectionPane connectionPane;
    private final ImageView logoView;
    private final ImageView logoViewSmall;

    public LogoPane() {
        logoView = ImageViewProvider
                .createImageView("/icons/bcozy.png", Constants.MAXLOGOWIDTH, Double.MAX_VALUE);
        logoViewSmall = ImageViewProvider.createImageView("/icons/bc.png", Constants.MIDDLE_ICON);

        connectionPane = new ConnectionPane();
        connectionPane.setAlignment(Pos.BOTTOM_RIGHT);
        connectionPane.getStyleClass().addAll("connection-indicator");


        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(logoView, connectionPane);
        this.setSpacing(30);


    }

    //@Override
    public Node getStatusIcon() {
        VBox statusIcons = new VBox(logoViewSmall, connectionPane.getStatusIcon());
        statusIcons.setAlignment(Pos.CENTER);
        statusIcons.setSpacing(20.0);
        return statusIcons;
    }

}
