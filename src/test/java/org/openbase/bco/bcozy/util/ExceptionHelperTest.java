package org.openbase.bco.bcozy.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author vdasilva
 */
public class ExceptionHelperTest {
    @Test
    public void getFromNested() throws Exception {
        Throwable cause = new RuntimeException("The Message");
        Exception exception = new Exception(cause);

        Assert.assertEquals(cause, ExceptionHelper.getCause(exception));
        Assert.assertEquals("The Message", ExceptionHelper.getCauseMessage(exception));
    }

    @Test
    public void get() throws Exception {
        Exception exception = new Exception("The Message");

        Assert.assertEquals(exception, ExceptionHelper.getCause(exception));
        Assert.assertEquals("The Message", ExceptionHelper.getCauseMessage(exception));
    }

}