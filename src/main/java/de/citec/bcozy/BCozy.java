/**
 * ==================================================================
 *
 * This file is part of BCozy.
 *
 * BCozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * BCozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BCozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package de.citec.bcozy;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main Class of the BCozy Program.
 */
public class BCozy extends Application {

    /**
     * Main Method starting JavaFX Environment.
     * @param args Arguments from commandline.
     */
    public static void main(final String... args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {

        final int screenWidth = 400;
        final int screenHeight = 600;
        primaryStage.setTitle("BCozy");
        final Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(final ActionEvent event) {
                //System.out.println("Hello World!");
            }
        });

        final StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, screenWidth, screenHeight));
        primaryStage.show();
    }
}
