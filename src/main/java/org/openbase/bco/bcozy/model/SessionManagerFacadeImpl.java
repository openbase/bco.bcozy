package org.openbase.bco.bcozy.model;

import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType;
import rst.domotic.unit.UnitTemplateType;
import rst.domotic.unit.authorizationgroup.AuthorizationGroupConfigType;
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
    public boolean registerUser(NewUser user, String plainPassword, boolean asAdmin, List<UnitConfigType
            .UnitConfig> groups) {
        try {
            return tryRegisterUser(user, plainPassword, asAdmin, groups);
        } catch (CouldNotPerformException | ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean tryRegisterUser(NewUser user, String plainPassword, boolean asAdmin,
                                    List<UnitConfigType.UnitConfig> groups)
            throws CouldNotPerformException, ExecutionException, InterruptedException, TimeoutException {

        UnitConfigType.UnitConfig unitConfig = tryCreateUser(user);

        SessionManager.getInstance().registerUser(
                unitConfig.getUserConfig().getUserName()/*unitConfig.getId()*/,

                plainPassword,
                asAdmin);

        for (UnitConfigType.UnitConfig group : groups) {
            tryAddToGroup(group, unitConfig.getId());
        }

        return true;
    }

    private UnitConfigType.UnitConfig tryCreateUser(NewUser user) throws CouldNotPerformException,
            InterruptedException, ExecutionException, TimeoutException {

        UnitConfigType.UnitConfig.Builder builder = UnitConfigType.UnitConfig.newBuilder();
        UserConfigType.UserConfig.Builder userConfigBuilder = UserConfigType.UserConfig.newBuilder();

        userConfigBuilder = userConfigBuilder
                .setUserName(user.getUsername())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName());

        UnitConfigType.UnitConfig unitConfig = builder
                .setUserConfig(userConfigBuilder.build())
                .setType(UnitTemplateType.UnitTemplate.UnitType.USER)//TODO: right way?
                .build();

        Future<UnitConfigType.UnitConfig> registeredUser = Registries.getUserRegistry().registerUserConfig(unitConfig);

        return registeredUser.get(1, TimeUnit.SECONDS);
    }

    private void tryAddToGroup(UnitConfigType.UnitConfig group, String userId) throws CouldNotPerformException,
            InterruptedException {
        
        UnitConfigType.UnitConfig.Builder unitConfig = Registries.getUserRegistry()
                .getAuthorizationGroupConfigById(group.getId()).toBuilder();
        AuthorizationGroupConfigType.AuthorizationGroupConfig.Builder authorizationGroupConfig = unitConfig
                .getAuthorizationGroupConfigBuilder();
        authorizationGroupConfig.addMemberId(userId);
        Registries.getUserRegistry().updateAuthorizationGroupConfig(unitConfig.build());
    }


    @Override
    public boolean userNameAvailable(String username) {
        try {
            Registries.getUserRegistry().getUserIdByUserName(username);
            return false;
        } catch (CouldNotPerformException | InterruptedException e) {
            LOGGER.info("Username %s already in use", username);
        }
        return true;
    }

    @Override
    public boolean passwordsValid(String password, String repeatedPassword) {
        // TODO other checks for pw validity? e.g. length..

        return password.equals(repeatedPassword);

    }

    @Override
    public boolean phoneIsValid(String phoneNumber) {
        //TODO check validity
        return true;
    }

    @Override
    public boolean mailIsValid(String mailAdress) {
        //Todo check validity
        return true;
    }
}
