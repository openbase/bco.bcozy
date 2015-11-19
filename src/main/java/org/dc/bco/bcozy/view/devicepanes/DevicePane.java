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
package org.dc.bco.bcozy.view.devicepanes;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.dc.bco.bcozy.view.Constants;

/**
 * Created by hoestreich on 11/19/15.
 */
public class DevicePane extends VBox {

    private final Label deviceName;
    private final Pane deviceContent;

    /**
     * Constructor for a device pane.
     * @param deviceName the name of the device
     * @param deviceContent the content (looks individually for each device)
     */
    public DevicePane(final String deviceName, final Pane deviceContent) {
        this.deviceName = new Label(deviceName);
        this.deviceContent = deviceContent;
        this.setSpacing(Constants.INSETS / 2);
        this.getChildren().addAll(new PaneElement(this.deviceContent), new PaneElement(this.deviceName));
    }
}
