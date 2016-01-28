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
package org.dc.bco.bcozy.jp;

import org.dc.jps.exception.JPNotAvailableException;
import org.dc.jps.preset.AbstractJPEnum;

import java.util.Locale;

/**
 * Created by hoestreich on 1/27/16.
 */
public class JPLanguage extends AbstractJPEnum<JPLanguage.Language> {

    /**
     * Language Enum.
     */
    public enum Language { DE, EN }

    /**
     * Command line argument strings.
     */
    public static final String[] COMMAND_IDENTIFIERS = {"--lang", "--language"};

    /**
     * Constructor for the JPLanguage class.
     */
    public JPLanguage() {
        super(COMMAND_IDENTIFIERS);
    }

    @Override
    protected Language getPropertyDefaultValue() throws JPNotAvailableException {
        if (Locale.getDefault().equals(Locale.GERMAN)) {
            return Language.DE;
        } else if (Locale.getDefault().equals(Locale.ENGLISH) || Locale.getDefault().equals(Locale.US)
            || Locale.getDefault().equals(Locale.UK) || Locale.getDefault().equals(Locale.CANADA)) {
            return Language.EN;
        } else {
            return Language.EN;
        }
    }

    @Override
    public String getDescription() {
        return "Language Property is used to configure the application language.";
    }
}
