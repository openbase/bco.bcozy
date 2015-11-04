import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application {

    private final double ZOOM_PANE_WIDTH = 1000;
    private final double ZOOM_PANE_HEIGHT = 1000;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) {
        Rectangle emptyHugeRectangle = new Rectangle(-(ZOOM_PANE_WIDTH/2), -(ZOOM_PANE_HEIGHT/2), ZOOM_PANE_WIDTH, ZOOM_PANE_HEIGHT);
        emptyHugeRectangle.setFill(Color.TRANSPARENT);

        Rectangle whiteRectangle = new Rectangle(0, 0, 25, 25);
        whiteRectangle.setFill(Color.RED);

        Circle blueCircle = new Circle(0, 0, 10, Color.BLUE);

        Group group = new Group(emptyHugeRectangle, whiteRectangle, blueCircle);
        Parent zoomPane = createZoomPane(group);

        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

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

        StackPane layout = new StackPane();
        layout.getChildren().setAll(zoomPane);

        BackgroundImage backgroundImage = new BackgroundImage(new Image("res/blueprint.jpg"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        layout.setBackground(new Background(backgroundImage));

        Scene scene = new Scene(layout);

        stage.setTitle("Location_Prototype");
        stage.setScene(scene);
        stage.show();

        this.centerScrollPaneToPoint((ScrollPane) zoomPane, new Point2D((minX+maxX)/2,(minY+maxY)/2));
    }

    private Parent createZoomPane(final Group group) {
        final double SCALE_DELTA = 1.05;
        final StackPane zoomPane = new StackPane();

        zoomPane.getChildren().add(group);

        final ScrollPane scroller = new ScrollPane();
        final Group scrollContent = new Group(zoomPane);
        scroller.setContent(scrollContent);
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.getStylesheets().add("view/transparent_scrollpane.css");

        //TODO: is this even needed?
        //scroller.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
        //    @Override
        //    public void changed(ObservableValue<? extends Bounds> observable,
        //                        Bounds oldValue, Bounds newValue) {
        //        zoomPane.setMinSize(newValue.getWidth(), newValue.getHeight());
        //    }
        //});

        scroller.setPrefViewportWidth(800);
        scroller.setPrefViewportHeight(600);

        zoomPane.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
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

            }
        });

        // Panning via drag....
        final ObjectProperty<Point2D> lastMouseCoordinates = new SimpleObjectProperty<Point2D>();
        scrollContent.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lastMouseCoordinates.set(new Point2D(event.getX(), event.getY()));
            }
        });

        scrollContent.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
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
            }
        });

        return scroller;
    }

    private void centerScrollPaneToPoint(ScrollPane scroller, Point2D center) {
        //TODO: not quite exact?!
        scroller.setHvalue((center.getX()+(ZOOM_PANE_WIDTH/2.0))/ZOOM_PANE_WIDTH);
        scroller.setVvalue((center.getY()+(ZOOM_PANE_HEIGHT/2.0))/ZOOM_PANE_HEIGHT);
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