import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import view.RoomPolygon;

public class Main extends Application {

    private final double ZOOM_PANE_WIDTH = 2000;
    private final double ZOOM_PANE_HEIGHT = 2000;

    private Group locationViewContent;
    private RoomPolygon selectedRoom;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) {
        Rectangle emptyHugeRectangle = new Rectangle(-(ZOOM_PANE_WIDTH/2), -(ZOOM_PANE_HEIGHT/2), ZOOM_PANE_WIDTH, ZOOM_PANE_HEIGHT);
        emptyHugeRectangle.setFill(Color.TRANSPARENT);

        //Dummy Room
        selectedRoom = new RoomPolygon(0.0, 0.0, 0.0, 0.0);

        RoomPolygon room0 = new RoomPolygon(50.0, 50.0,
                100.0, 50.0,
                100.0, 100.0,
                80.0, 100.0,
                80.0, 80.0,
                50.0, 80.0);

        RoomPolygon room1 = new RoomPolygon(-10.0, -10.0,
                -10.0, 10.0,
                30.0, 30.0,
                30.0, -10.0);

        RoomPolygon room2 = new RoomPolygon(50.0, -20.0,
                100.0, -20.0,
                100.0, 30.0,
                60.0, 30.0,
                60.0, 10.0,
                50.0, 10.0);

        RoomPolygon room3 = new RoomPolygon(-30.0, 50.0,
                -10.0, 70.0,
                -10.0, 90.0,
                -30.0, 110.0,
                -50.0, 110.0,
                -70.0, 90.0,
                -70.0, 70.0,
                -50.0, 50.0);

        locationViewContent = new Group(emptyHugeRectangle, room0, room1, room2, room3);
        ScrollPane scrollPane = createZoomPane(locationViewContent);

        StackPane layout = new StackPane();
        layout.getChildren().setAll(scrollPane);

        BackgroundImage backgroundImage = new BackgroundImage(new Image("res/blueprint.jpg"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        layout.setBackground(new Background(backgroundImage));

        Scene scene = new Scene(layout);

        stage.setTitle("Location_Prototype");
        stage.setScene(scene);
        stage.show();

        this.centerScrollPaneToPoint(scrollPane, this.getCenterOfGroup(locationViewContent, true));
        this.addMouseEventHandlerToRoom(scrollPane, room0, room1, room2, room3);
    }

    private ScrollPane createZoomPane(final Group group) {
        final double SCALE_DELTA = 1.05;
        final StackPane zoomPane = new StackPane();

        zoomPane.getChildren().add(group);

        final ScrollPane scroller = new ScrollPane();
        final Group scrollContent = new Group(zoomPane);
        scroller.setContent(scrollContent);
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.getStylesheets().add("view/transparent_scrollpane.css");

        //TODO: what iiiiiis is good for?
        scroller.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            zoomPane.setMinSize(newValue.getWidth(), newValue.getHeight());
        });

        scroller.setPrefViewportWidth(800);
        scroller.setPrefViewportHeight(600);

        zoomPane.setOnScroll(event -> {
            event.consume();

            if (event.getDeltaY() == 0) {
                return;
            }

            double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA
                    : 1 / SCALE_DELTA;

            // amount of scrolling in each direction in scrollContent coordinate
            // units
            Point2D scrollOffset = figureScrollOffset(scrollContent, scroller);

            group.setScaleX(group.getScaleX() * scaleFactor);
            group.setScaleY(group.getScaleY() * scaleFactor);

            // move viewport so that old center remains in the center after the
            // scaling
            repositionScroller(scrollContent, scroller, scaleFactor, scrollOffset);

        });

        // Panning via drag....
        final ObjectProperty<Point2D> lastMouseCoordinates = new SimpleObjectProperty<>();
        scrollContent.setOnMousePressed(event -> lastMouseCoordinates.set(new Point2D(event.getX(), event.getY())));

        scrollContent.setOnMouseDragged(event -> {
            double deltaX = event.getX() - lastMouseCoordinates.get().getX();
            double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
            double deltaH = deltaX * (scroller.getHmax() - scroller.getHmin()) / extraWidth;
            double desiredH = scroller.getHvalue() - deltaH;
            scroller.setHvalue(Math.max(0, Math.min(scroller.getHmax(), desiredH)));

            double deltaY = event.getY() - lastMouseCoordinates.get().getY();
            double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
            double deltaV = deltaY * (scroller.getHmax() - scroller.getHmin()) / extraHeight;
            double desiredV = scroller.getVvalue() - deltaV;
            scroller.setVvalue(Math.max(0, Math.min(scroller.getVmax(), desiredV)));
        });

        return scroller;
    }

    private void centerScrollPaneToPoint(ScrollPane scroller, Point2D center) {
        double realZoomPaneWidth = ZOOM_PANE_WIDTH - scroller.getWidth()/locationViewContent.getScaleX();
        double realZoomPaneHeight = ZOOM_PANE_HEIGHT - scroller.getHeight()/locationViewContent.getScaleY();
        scroller.setHvalue((center.getX()+(realZoomPaneWidth/2.0))/realZoomPaneWidth);
        scroller.setVvalue((center.getY()+(realZoomPaneHeight/2.0))/realZoomPaneHeight);
    }

    private void centerScrollPaneToPointAnimated(ScrollPane scroller, Point2D center) {
        double realZoomPaneWidth = ZOOM_PANE_WIDTH - scroller.getWidth()/locationViewContent.getScaleX();
        double realZoomPaneHeight = ZOOM_PANE_HEIGHT - scroller.getHeight()/locationViewContent.getScaleY();

        final Timeline timeline = new Timeline();
        timeline.setCycleCount(1);

        final KeyValue keyValueX = new KeyValue(scroller.hvalueProperty(), (center.getX()+(realZoomPaneWidth/2.0))/realZoomPaneWidth, Interpolator.EASE_BOTH);
        final KeyValue keyValueY = new KeyValue(scroller.vvalueProperty(), (center.getY()+(realZoomPaneHeight/2.0))/realZoomPaneHeight, Interpolator.EASE_BOTH);
        final KeyFrame keyFrame = new KeyFrame(Duration.millis(500), keyValueX, keyValueY);

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    private void addMouseEventHandlerToRoom(ScrollPane scrollPane, RoomPolygon room) {
        room.setOnMouseClicked(event -> {
            event.consume();

            centerScrollPaneToPointAnimated(scrollPane, new Point2D(room.getCenterX(), room.getCenterY()));
            if (!room.isSelected()) {
                selectedRoom.toggleSelected();
                room.toggleSelected();
                selectedRoom = room;
            }
        });
    }

    private void addMouseEventHandlerToRoom(ScrollPane scrollPane, RoomPolygon... room) {
        for (RoomPolygon currentRoom : room) this.addMouseEventHandlerToRoom(scrollPane, currentRoom);
    }

    private Point2D getCenterOfGroup (Group group, boolean skipFirst) {
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        if (skipFirst) {
            for (int i = 1; i < group.getChildren().size(); i++) {
                if (group.getChildren().get(i).getLayoutBounds().getMaxX() > maxX)
                    maxX = group.getChildren().get(i).getLayoutBounds().getMaxX();
                if (group.getChildren().get(i).getLayoutBounds().getMaxY() > maxY)
                    maxY = group.getChildren().get(i).getLayoutBounds().getMaxY();
                if (group.getChildren().get(i).getLayoutBounds().getMinX() < minX)
                    minX = group.getChildren().get(i).getLayoutBounds().getMinX();
                if (group.getChildren().get(i).getLayoutBounds().getMinY() < minY)
                    minY = group.getChildren().get(i).getLayoutBounds().getMinY();
            }
        } else {
            for (int i = 0; i < group.getChildren().size(); i++) {
                if (group.getChildren().get(i).getLayoutBounds().getMaxX() > maxX)
                    maxX = group.getChildren().get(i).getLayoutBounds().getMaxX();
                if (group.getChildren().get(i).getLayoutBounds().getMaxY() > maxY)
                    maxY = group.getChildren().get(i).getLayoutBounds().getMaxY();
                if (group.getChildren().get(i).getLayoutBounds().getMinX() < minX)
                    minX = group.getChildren().get(i).getLayoutBounds().getMinX();
                if (group.getChildren().get(i).getLayoutBounds().getMinY() < minY)
                    minY = group.getChildren().get(i).getLayoutBounds().getMinY();
            }
        }

        return new Point2D((minX+maxX)/2,(minY+maxY)/2);
    }

    private Point2D figureScrollOffset(Node scrollContent, ScrollPane scroller) {
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        double hScrollProportion = (scroller.getHvalue() - scroller.getHmin()) / (scroller.getHmax() - scroller.getHmin());
        double scrollXOffset = hScrollProportion * Math.max(0, extraWidth);
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        double vScrollProportion = (scroller.getVvalue() - scroller.getVmin()) / (scroller.getVmax() - scroller.getVmin());
        double scrollYOffset = vScrollProportion * Math.max(0, extraHeight);
        return new Point2D(scrollXOffset, scrollYOffset);
    }

    private void repositionScroller(Node scrollContent, ScrollPane scroller, double scaleFactor, Point2D scrollOffset) {
        double scrollXOffset = scrollOffset.getX();
        double scrollYOffset = scrollOffset.getY();
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        if (extraWidth > 0) {
            double halfWidth = scroller.getViewportBounds().getWidth() / 2 ;
            double newScrollXOffset = (scaleFactor - 1) *  halfWidth + scaleFactor * scrollXOffset;
            scroller.setHvalue(scroller.getHmin() + newScrollXOffset * (scroller.getHmax() - scroller.getHmin()) / extraWidth);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        if (extraHeight > 0) {
            double halfHeight = scroller.getViewportBounds().getHeight() / 2 ;
            double newScrollYOffset = (scaleFactor - 1) * halfHeight + scaleFactor * scrollYOffset;
            scroller.setVvalue(scroller.getVmin() + newScrollYOffset * (scroller.getVmax() - scroller.getVmin()) / extraHeight);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
    }
}