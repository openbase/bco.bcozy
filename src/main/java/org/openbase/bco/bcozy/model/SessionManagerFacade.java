package org.openbase.bco.bcozy.model;

import rst.domotic.unit.UnitConfigType;

import java.util.List;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.VerificationFailedException;

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

    void registerUser(final NewUser user, final String plainPassword, boolean asAdmin, List<UnitConfigType.UnitConfig> groups) throws CouldNotPerformException;

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

    // TODO: @vanessa Why is this type needed? Because the UserConfig contains all these informations already.
    class NewUser {

        private final String username;
        private final String firstName;
        private final String lastName;
        private final String mail;
        private final String phone;

        public NewUser(String username, String firstName, String lastName, String mail, String phone) {
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.mail = mail;
            this.phone = phone;
        }

        public String getUsername() {
            return username;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getMail() {
            return mail;
        }

        public String getPhone() {
            return phone;
        }
    }
}
