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

package org.openbase.bco.bcozy.view.location;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.connection.ConnectionDataType;

/**
 *
 */
public class PassagePolygon extends ConnectionPolygon {

    /**
     * Constructor for the PassagePolygon.
     */
    public PassagePolygon(final LocationMap locationMap) throws InstantiationException {
        super(locationMap);
    }

    @Override
    public void applyConfigUpdate(UnitConfig unitConfig) throws InterruptedException {
        super.applyConfigUpdate(unitConfig);
        
        // The following Code adjusts passagePolygon to properly cut out room walls.
        // Yes, it is unfortunately ugly as hell... :(
        if (isHorizontal()) {
            if ( getPoints() .get(0).equals( getPoints() .get(2))) { //Start Top Right or Bottom Left
                 getPoints() .set(1,  getPoints() .get(1) - Constants.ROOM_STROKE_WIDTH / 2);
                 getPoints() .set(3,  getPoints() .get(3) + Constants.ROOM_STROKE_WIDTH / 2);
                 getPoints() .set(5,  getPoints() .get(5) + Constants.ROOM_STROKE_WIDTH / 2);
                 getPoints() .set(7,  getPoints() .get(7) - Constants.ROOM_STROKE_WIDTH / 2);
                if ( getPoints() .get(0) <  getPoints() .get(4)) { //Start Bottom Left
                     getPoints() .set(0,  getPoints() .get(0) + Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(2,  getPoints() .get(2) + Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(4,  getPoints() .get(4) - Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(6,  getPoints() .get(6) - Constants.ROOM_STROKE_WIDTH / 2);
                } else { // Start Top Right
                     getPoints() .set(0,  getPoints() .get(0) - Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(2,  getPoints() .get(2) - Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(4,  getPoints() .get(4) + Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(6,  getPoints() .get(6) + Constants.ROOM_STROKE_WIDTH / 2);
                }
            } else { //Start Top Left or Bottom Right
                 getPoints() .set(1,  getPoints() .get(1) - Constants.ROOM_STROKE_WIDTH / 2);
                 getPoints() .set(3,  getPoints() .get(3) - Constants.ROOM_STROKE_WIDTH / 2);
                 getPoints() .set(5,  getPoints() .get(5) + Constants.ROOM_STROKE_WIDTH / 2);
                 getPoints() .set(7,  getPoints() .get(7) + Constants.ROOM_STROKE_WIDTH / 2);
                if ( getPoints() .get(0) <  getPoints() .get(4)) { //Start Top Left
                     getPoints() .set(0,  getPoints() .get(0) + Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(2,  getPoints() .get(2) - Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(4,  getPoints() .get(4) - Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(6,  getPoints() .get(6) + Constants.ROOM_STROKE_WIDTH / 2);
                } else { // Start Bottom Right
                     getPoints() .set(0,  getPoints() .get(0) - Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(2,  getPoints() .get(2) + Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(4,  getPoints() .get(4) + Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(6,  getPoints() .get(6) - Constants.ROOM_STROKE_WIDTH / 2);
                }
            }
        } else {
            if ( getPoints() .get(1).equals( getPoints() .get(3))) { //Start Top Left or Bottom Right
                 getPoints() .set(0,  getPoints() .get(0) - Constants.ROOM_STROKE_WIDTH / 2);
                 getPoints() .set(2,  getPoints() .get(2) + Constants.ROOM_STROKE_WIDTH / 2);
                 getPoints() .set(4,  getPoints() .get(4) + Constants.ROOM_STROKE_WIDTH / 2);
                 getPoints() .set(6,  getPoints() .get(6) - Constants.ROOM_STROKE_WIDTH / 2);
                if ( getPoints() .get(1) <  getPoints() .get(5)) { //Start Top Left
                     getPoints() .set(1,  getPoints() .get(1) + Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(3,  getPoints() .get(3) + Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(5,  getPoints() .get(5) - Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(7,  getPoints() .get(7) - Constants.ROOM_STROKE_WIDTH / 2);
                } else { // Start Bottom Right
                     getPoints() .set(1,  getPoints() .get(1) - Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(3,  getPoints() .get(3) - Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(5,  getPoints() .get(5) + Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(7,  getPoints() .get(7) + Constants.ROOM_STROKE_WIDTH / 2);
                }
            } else { //Start Top Right or Bottom Left
                 getPoints() .set(0,  getPoints() .get(0) + Constants.ROOM_STROKE_WIDTH / 2);
                 getPoints() .set(2,  getPoints() .get(2) + Constants.ROOM_STROKE_WIDTH / 2);
                 getPoints() .set(4,  getPoints() .get(4) - Constants.ROOM_STROKE_WIDTH / 2);
                 getPoints() .set(6,  getPoints() .get(6) - Constants.ROOM_STROKE_WIDTH / 2);
                if ( getPoints() .get(1) <  getPoints() .get(5)) { //Start Top Right
                     getPoints() .set(1,  getPoints() .get(1) + Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(3,  getPoints() .get(3) - Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(5,  getPoints() .get(5) - Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(7,  getPoints() .get(7) + Constants.ROOM_STROKE_WIDTH / 2);
                } else { // Start Bottom Left
                     getPoints() .set(1,  getPoints() .get(1) - Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(3,  getPoints() .get(3) + Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(5,  getPoints() .get(5) + Constants.ROOM_STROKE_WIDTH / 2);
                     getPoints() .set(7,  getPoints() .get(7) - Constants.ROOM_STROKE_WIDTH / 2);
                }
            }
        }
    }

    @Override
    public void applyDataUpdate(ConnectionDataType.ConnectionData unitData) {
    }
    
    @Override
    protected void setConnectionStyle() {
        this.setMainColor(Constants.PASSAGE_FILL);
        this.setStrokeWidth(0);
        this.setMouseTransparent(true);
    }

    /**
     * Will be called when either the main or the custom color changes.
     * The initial values for both colors are Color.TRANSPARENT.
     * @param mainColor   The main color
     * @param customColor The custom color
     */
    @Override
    protected void onColorChange(final Color mainColor, final Color customColor) {
        if (customColor.equals(Color.TRANSPARENT)) {
            this.setFill(mainColor);
        } else {
            this.setFill(mainColor.interpolate(customColor, CUSTOM_COLOR_WEIGHT));
        }
    }
}
