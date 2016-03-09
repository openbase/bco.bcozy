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
package org.dc.bco.bcozy.model;

import java.util.Locale;
import java.util.Observable;

/**
 * Created by hoestreich on 1/2/16.
 */
public final class LanguageSelection extends Observable {

    /**
     * Singleton instance.
     */
    private static LanguageSelection instance;

    /**
     * Private constructor to deny manual instantiation.
     */
    private LanguageSelection() { }

    /**
     * Singleton Pattern.
     * @return the singleton instance of the language selection observable
     */
    public static LanguageSelection getInstance() {
        synchronized (LanguageSelection.class) {
            if (LanguageSelection.instance == null) {
                LanguageSelection.instance = new LanguageSelection();
            }
        }
        return LanguageSelection.instance;
    }

    /**
     * Setter method to allow changing the language and notifying all gui elements to adapt afterwards.
     * @param selectedLocale the new locale which should be set as default.
     */
    public void setSelectedLocale(final Locale selectedLocale) {
        this.setChanged();
        Locale.setDefault(selectedLocale);
        notifyObservers(Locale.getDefault());
    }
}
