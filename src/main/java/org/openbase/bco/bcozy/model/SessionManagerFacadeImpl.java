package org.openbase.bco.bcozy.model;

import com.google.protobuf.ProtocolStringList;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.VerificationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateType;
import rst.domotic.unit.authorizationgroup.AuthorizationGroupConfigType.AuthorizationGroupConfig;
import rst.domotic.unit.user.UserConfigType;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author vdasilva
 */
public class SessionManagerFacadeImpl implements SessionManagerFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionManagerFacadeImpl.class);

    @Override
    public boolean isAdmin() {
        return SessionManager.getInstance().isAdmin();
    }

    @Override
    public void registerUser(final UserConfigType.UserConfig user, final String plainPassword, boolean asAdmin, List<UnitConfigType
            .UnitConfig> groups) throws CouldNotPerformException {
        try {
            tryRegisterUser(user, plainPassword, asAdmin, groups);
        } catch (CouldNotPerformException | ExecutionException | InterruptedException | TimeoutException ex) {
            throw new CouldNotPerformException("Could not register user!", ex);
        }
    }

    private void tryRegisterUser(UserConfigType.UserConfig user, String plainPassword, boolean asAdmin, List<UnitConfigType.UnitConfig> groups) throws CouldNotPerformException, ExecutionException, InterruptedException, TimeoutException {

        UnitConfigType.UnitConfig unitConfig = tryCreateUser(user);

        try {
            SessionManager.getInstance().registerUser(
                    unitConfig.getId(),
                    plainPassword,
                    asAdmin);
        } catch (CouldNotPerformException ex) {
            // If adding to the credential storage failed, remove the user from the registry to prevent inconsistencies.
            Registries.getUserRegistry().removeUserConfig(unitConfig);
            throw ex;
        }

        try {
            for (UnitConfigType.UnitConfig group : groups) {
                tryAddToGroup(group, unitConfig.getId());
            }
        } catch (CouldNotPerformException | InterruptedException ex) {
            // If adding to a group failed, remove the user from all groups...
            for (UnitConfigType.UnitConfig group : groups) {
                tryRemoveFromGroup(group, unitConfig.getId());
            }
            // ... from the credential storage...
            SessionManager.getInstance().removeUser(unitConfig.getId()/*unitConfig.getId()*/);

            // ... and from the registry.
            Registries.getUserRegistry().removeUserConfig(unitConfig);
            throw ex;
        }
    }

    private UnitConfigType.UnitConfig tryCreateUser(UserConfigType.UserConfig user) throws CouldNotPerformException, InterruptedException, ExecutionException, TimeoutException {

        UnitConfigType.UnitConfig.Builder builder = UnitConfigType.UnitConfig.newBuilder();

        UnitConfigType.UnitConfig unitConfig = builder
                .setUserConfig(user)
                .setType(UnitTemplateType.UnitTemplate.UnitType.USER)//TODO: right way?
                .build();

        Future<UnitConfigType.UnitConfig> registeredUser = Registries.getUserRegistry().registerUserConfig(unitConfig);

        return registeredUser.get(5, TimeUnit.SECONDS);
    }

    private void tryAddToGroup(UnitConfigType.UnitConfig group, String userId) throws CouldNotPerformException, InterruptedException {
        UnitConfig.Builder unitConfig = Registries.getUserRegistry().getAuthorizationGroupConfigById(group.getId()).toBuilder();
        AuthorizationGroupConfig.Builder authorizationGroupConfig = unitConfig.getAuthorizationGroupConfigBuilder();
        authorizationGroupConfig.addMemberId(userId);
        Registries.getUserRegistry().updateAuthorizationGroupConfig(unitConfig.build());
    }

    private void tryRemoveFromGroup(UnitConfigType.UnitConfig group, String userId) throws CouldNotPerformException, InterruptedException {

        UnitConfig.Builder unitConfig = Registries.getUserRegistry().getAuthorizationGroupConfigById(group.getId()).toBuilder();
        AuthorizationGroupConfig.Builder authorizationGroupConfig = unitConfig.getAuthorizationGroupConfigBuilder();

        ProtocolStringList members = authorizationGroupConfig.getMemberIdList();

        authorizationGroupConfig.clearMemberId();

        for (String member : members) {
            if (!member.equals(userId)) {
                authorizationGroupConfig.addMemberId(member);
            }
        }

        Registries.getUserRegistry().updateAuthorizationGroupConfig(unitConfig.build());
    }

    @Override
    public void verifyUserName(String username) throws VerificationFailedException, InterruptedException {
        try {
            // Registries.getUserRegistry().getUserIdByUserName(username);

            if (username.isEmpty()){
                throw new VerificationFailedException("Username must not be empty!");
            }

            // ##### reimplemented because not included in current master api.
            for (final UnitConfig userUnitConfig : Registries.getUserRegistry().getUserConfigs()) {
                if (userUnitConfig.getUserConfig().getUserName().equals(username)) {
                    throw new VerificationFailedException("Username[" + username + "] already in use!");
                }
            }
        } catch (CouldNotPerformException ex) {
            LOGGER.warn("Could not verify user name!", ex);
            throw new VerificationFailedException("Could not verify user name!", ex);
        }
    }

    @Override
    public void verifyPasswords(String password, String repeatedPassword) throws VerificationFailedException {
        // TODO other checks for pw validity? e.g. length..
        if (!password.equals(repeatedPassword)) {
            throw new VerificationFailedException("repeated password does not match!");
        }
    }

    @Override
    public void verifyPhoneNumber(String phoneNumber) throws VerificationFailedException {
        //TODO check validity
        // throw new VerificationFailedException("not valid because of ...");
    }

    @Override
    public void verifyMailAddress(String mailAddress) throws VerificationFailedException {
        //Todo check validity
        // throw new VerificationFailedException("not valid because of ...");
        if (mailAddress.isEmpty()) {
            throw new VerificationFailedException("E-Mail Adress must not be empty!");
        }
    }
}
