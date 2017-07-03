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
package org.openbase.bco.bcozy.view;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * @author hoestreich
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public final class AnimationProvider {

    /**
     * Method to create a FadeTransition with several parameters.
     *
     * @param node the node to which the transition should be applied
     * @param fromValue the opacity value from which the transition should start
     * @param toValue the opactity value where the transition should end
     * @param cycleCount the number of times the animation should be played (use Animation.INDEFINITE for endless)
     * @param duration the duration which one animation cycle should take
     * @return an instance of the created FadeTransition
     */
    public static FadeTransition createFadeTransition(final Node node, final double fromValue, final double toValue, final int cycleCount, final double duration) {
        assert node != null;
        
        final FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), node);
        fadeTransition.setFromValue(fromValue);
        fadeTransition.setToValue(toValue);
        fadeTransition.setCycleCount(cycleCount);
        fadeTransition.setAutoReverse(true);
        return fadeTransition;
    }
}
