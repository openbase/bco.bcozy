package org.openbase.bco.bcozy.view.location;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import org.openbase.jul.exception.CouldNotPerformException;

public interface LocationMap {

    void selectRootLocation();

    boolean isInitialized();

    Pane getEditOverlay();

    boolean isLocationSelected();

    DynamicPolygon getLastClickTarget();

    void setSelectedUnit(DynamicPolygon newSelectedPolygon) throws CouldNotPerformException;

    /**
     * ZoomFits to the root if available. Otherwise to the first location in the
     * tileMap.
     */
    void zoomFit();

    void addSelectedUnitListener(final ChangeListener<? super DynamicUnitPolygon> changeListener);

    void removeSelectedUnitListener(final ChangeListener<? super DynamicUnitPolygon> changeListener);

    void autoFocusPolygon(DynamicPolygon polygon);

    double computeScale(DynamicPolygon polygon);

    void autoFocusPolygonAnimated(DynamicPolygon polygon);

    Point2D calculateTransition(double scale, DynamicPolygon polygon);

    void handleHoverUpdate(DynamicPolygon locationPolygon, boolean hover);

    void selectAnchorPoint(final AnchorPoint anchorPoint);

    void deselectAnchorPoint(final AnchorPoint anchorPoint);

    boolean isSelected(final AnchorPoint anchorPoint);
}
