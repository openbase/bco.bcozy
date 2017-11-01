package org.openbase.bco.bcozy.permissions;

import org.openbase.bco.bcozy.model.LanguageSelection;

import java.util.logging.Logger;

/**
 * @author vdasilva
 */
public final class OtherPermissionsViewModel extends AbstractTableRowPermissionsViewModel {
    private static final Logger LOG = Logger.getLogger(OtherPermissionsViewModel.class.getName());

    public OtherPermissionsViewModel(boolean read, boolean write, boolean access) {
        super(LanguageSelection.getLocalized("permissions.other"), read, write, access);
    }

}
