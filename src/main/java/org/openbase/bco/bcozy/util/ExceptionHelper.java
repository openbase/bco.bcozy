package org.openbase.bco.bcozy.util;

/**
 * @author vdasilva
 */
public class ExceptionHelper {

    private ExceptionHelper() {
    }

    public static String getCauseMessage(final Exception ex) {
        return getCause(ex).getMessage();
    }

    public static Throwable getCause(final Exception ex) {
        Throwable cause = ex;

        while (cause.getCause() != null) {
            cause = ex.getCause();
        }

        return cause;
    }
}
