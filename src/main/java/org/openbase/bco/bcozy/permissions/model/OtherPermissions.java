package org.openbase.bco.bcozy.permissions.model;

import org.openbase.bco.bcozy.model.LanguageSelection;

import java.util.logging.Logger;

/**
 * @author vdasilva
 */
public final class OtherPermissions extends AbstractPermissions {
    private static final Logger LOG = Logger.getLogger(OtherPermissions.class.getName());

    public OtherPermissions(boolean read, boolean write, boolean access) {
        super(LanguageSelection.buildLabel("permissions.other"), read, write, access);
    }
}
