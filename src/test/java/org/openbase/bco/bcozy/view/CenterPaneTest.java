package org.openbase.bco.bcozy.view;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.Ignore;

/**
 * @author vdasilva
 */
public class CenterPaneTest extends Application {

    FloatingPopUp popUp;


    @Ignore
    public void test() {
        CenterPaneTest.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane pane = new BorderPane();
        Scene scene = new Scene(pane, 300, 300);
        primaryStage.setScene(scene);


        /*
        CenterPane centerPane = new CenterPane();
        pane.setCenter(centerPane);
        centerPane.setViewSwitchingButtonsVisible(true);
        new CenterPaneController(centerPane);
        */

        popUp = new FloatingPopUp(Pos.TOP_CENTER);
        popUp.addParentElement(MaterialIcon.SETTINGS, this::setMaximizeAction);
        popUp.addElement(MaterialDesignIcon.THERMOMETER_LINES, this::setMaximizeAction);
        popUp.addElement(MaterialIcon.VISIBILITY, this::setMaximizeAction);
        popUp.addElement(MaterialIcon.ACCESS_ALARM, event -> System.out.println("ACCESS_ALARM"));
        pane.setLeft(popUp);


        popUp = new FloatingPopUp(Pos.TOP_CENTER);
        popUp.addElement(MaterialIcon.FULLSCREEN, this::setMaximizeAction);
        popUp.addParentElement(MaterialIcon.FULLSCREEN_EXIT, this::setMaximizeAction);
        pane.setRight(popUp);

        primaryStage.show();

    }

    private void setMaximizeAction() {
        final Stage stage = (Stage) popUp.getScene().getWindow();
        if (stage.isFullScreen()) {
            stage.setFullScreen(false);
        } else {
            stage.setFullScreen(true);
        }
    }
}