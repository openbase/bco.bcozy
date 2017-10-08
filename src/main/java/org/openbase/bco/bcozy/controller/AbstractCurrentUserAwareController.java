package org.openbase.bco.bcozy.controller;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import org.openbase.bco.authentication.lib.SessionManager;

/**
 * Abstract Controller, which is aware of the current UserID.
 *
 * @author vdasilva
 */
public abstract class AbstractCurrentUserAwareController {

    private final ReadOnlyStringWrapper userId = new ReadOnlyStringWrapper();

    protected AbstractCurrentUserAwareController() {
        SessionManager.getInstance().addLoginObserver((o, b) ->
                userId.set(SessionManager.getInstance().getUserId()));
    }

    public String getUserId() {
        return userId.get();
    }

    public ReadOnlyStringProperty userIdProperty() {
        return userId.getReadOnlyProperty();
    }
}
