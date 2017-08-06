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
    public boolean registerUser(String username, String plainPassword, boolean asAdmin, List<UnitConfigType
            .UnitConfig> groups) {
        try {
            return tryRegisterUser(username, plainPassword, asAdmin, groups);
        } catch (CouldNotPerformException | ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean tryRegisterUser(String username, String plainPassword, boolean asAdmin,
                                    List<UnitConfigType.UnitConfig> groups)
            throws CouldNotPerformException, ExecutionException, InterruptedException, TimeoutException {

        UnitConfigType.UnitConfig unitConfig = tryCreateUser(username);

        SessionManager.getInstance().registerUser(unitConfig.getId(), plainPassword, asAdmin);

        for (UnitConfigType.UnitConfig group : groups) {
            tryAddToGroup(group, unitConfig.getId());
        }

        return true;
    }

    private UnitConfigType.UnitConfig tryCreateUser(String username) throws CouldNotPerformException,
            InterruptedException, ExecutionException, TimeoutException {

        UnitConfigType.UnitConfig.Builder builder = UnitConfigType.UnitConfig.newBuilder();
        UserConfigType.UserConfig.Builder userConfigBuilder = UserConfigType.UserConfig.newBuilder();
        //builder.getUserConfigBuilder();

        userConfigBuilder = userConfigBuilder
                .setUserName(username)
                .setFirstName(username/*TODO: real Firstname*/)
                .setLastName("username"/*TODO: real Lastname*/);

        UnitConfigType.UnitConfig unitConfig = builder
                .setUserConfig(userConfigBuilder.build())
                .setType(UnitTemplateType.UnitTemplate.UnitType.USER)//TODO: right way?
                .build();

        Future<UnitConfigType.UnitConfig> user = Registries.getUserRegistry().registerUserConfig(unitConfig);
        //return unitConfig;
        return user.get(1, TimeUnit.SECONDS);
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
            Registries.getUserRegistry().getUserConfigByUserName(username);
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
}
