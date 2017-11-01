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

    @Test
    public void verifyUnequalPasswords() throws VerificationFailedException {
        try {
            sessionManagerFacade.verifyPasswords("test", "tset");
            Assert.fail("no VerificationFailedException, fail test");
        } catch (VerificationFailedException ex) {
            Assert.assertEquals("repeated password does not match!", ex.getMessage());
        }
    }

    @Test
    public void verifyEmptyPasswords() throws VerificationFailedException {
        try {
            sessionManagerFacade.verifyPasswords("", "");
            Assert.fail("no VerificationFailedException, fail test");
        } catch (VerificationFailedException ex) {
            Assert.assertEquals("Password must not be empty!", ex.getMessage());
        }
    }


    @Test()
    public void verifyPasswords() throws VerificationFailedException {
        sessionManagerFacade.verifyPasswords("test", "test");
    }
}