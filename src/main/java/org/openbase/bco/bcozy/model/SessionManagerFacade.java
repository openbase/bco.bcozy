package org.openbase.bco.bcozy.model;

import rst.domotic.unit.UnitConfigType;

import java.util.List;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.VerificationFailedException;
import rst.domotic.unit.user.UserConfigType;

/**
 * @author vdasilva
 */
public interface SessionManagerFacade {

    /**
     * Checks, if the current user is an admin.
     *
     * @return true, if the current user is an admin, false otherwise
     */
    boolean isAdmin();

    void registerUser(final UserConfigType.UserConfig user, final String plainPassword, boolean asAdmin, List<UnitConfigType.UnitConfig>
            groups) throws CouldNotPerformException;

    /**
     * Checks, if the username is available.
     *
     * @param username the username to check
     * @throws VerificationFailedException is thrown if the username is already in use.
     */
    void verifyUserName(final String username) throws VerificationFailedException, InterruptedException;

    /**
     * Validates the given password and compares it with the repeated password.
     *
     * @param password the password to validate
     * @param repeatedPassword the repeated password
     * @throws VerificationFailedException is thrown if the {@code password} is not valid or does not matches repeatedPassword.
     */
    void verifyPasswords(final String password, final String repeatedPassword) throws VerificationFailedException;

    void verifyPhoneNumber(final String phoneNumber) throws VerificationFailedException;

    void verifyMailAddress(final String mailAddress) throws VerificationFailedException;

}
