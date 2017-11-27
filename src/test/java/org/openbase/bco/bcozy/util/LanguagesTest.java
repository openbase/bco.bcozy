package org.openbase.bco.bcozy.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author vdasilva
 */
public class LanguagesTest {

    @Test
    public void get() throws Exception {
        Languages languages = new Languages();

        Assert.assertEquals(2, languages.get().size());
    }

}
