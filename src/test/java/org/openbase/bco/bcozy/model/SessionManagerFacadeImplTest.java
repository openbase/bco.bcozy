package org.openbase.bco.bcozy.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openbase.jul.exception.VerificationFailedException;

/**
 * @author vdasilva
 */
public class SessionManagerFacadeImplTest {
    SessionManagerFacadeImpl sessionManagerFacade;

    @Before
    public void setUp() throws Exception {
        sessionManagerFacade = new SessionManagerFacadeImpl();
    }

    @Test(expected = VerificationFailedException.class)
    public void verifyUnequalPasswords() throws VerificationFailedException {
        sessionManagerFacade.verifyPasswords("test", "tset");

        Assert.fail("no VerificationFailedException, fail test");
    }

    @Test(expected = VerificationFailedException.class)
    public void verifyEmptyPasswords() throws VerificationFailedException {
        sessionManagerFacade.verifyPasswords("", "");

        Assert.fail("no VerificationFailedException, fail test");
    }


    @Test()
    public void verifyPasswords() throws VerificationFailedException {
        sessionManagerFacade.verifyPasswords("test", "test");

        Assert.assertTrue("no VerificationFailedException, test succeeded", true);
    }
}