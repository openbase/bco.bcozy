package org.openbase.bco.bcozy.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.openbase.bco.authentication.lib.CachedAuthenticationRemote;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.bcozy.util.AuthorizationGroups;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType;
import rst.domotic.unit.user.UserConfigType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author vdasilva
 */
public class UserData {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserData.class);

    private final StringProperty userId = new SimpleStringProperty("");
    private final StringProperty phone = new SimpleStringProperty("");

    private final String originalUserName;
    private final StringProperty userName = new SimpleStringProperty("");
    private final StringProperty mail = new SimpleStringProperty("");
    private final StringProperty firstname = new SimpleStringProperty("");
    private final StringProperty lastName = new SimpleStringProperty("");
    private final BooleanProperty occupant = new SimpleBooleanProperty(false);
    private final BooleanProperty admin = new SimpleBooleanProperty(false);
    private final List<UnitConfigType.UnitConfig> groups = new ArrayList<>();


    public static UserData currentUser() throws CouldNotPerformException, InterruptedException, TimeoutException, ExecutionException {
        String userId = SessionManager.getInstance().getUserId();
        UnitConfigType.UnitConfig userConfig = Registries.getUserRegistry().getUserConfigById(userId);

        return new UserData(userConfig);
    }

    /**
     * UserData for non-existing-user.
     */
    public UserData() {
        userId.setValue(null);
        originalUserName = "";
    }

    public UserData(UnitConfigType.UnitConfig unitConfig) throws CouldNotPerformException, InterruptedException, TimeoutException, ExecutionException {
        userId.setValue(unitConfig.getId());
        originalUserName = unitConfig.getUserConfig().getUserName();

        updateValues(unitConfig.getUserConfig());
        Registries.getUserRegistry().addDataObserver((source, data) -> updateValues());
    }

    private void updateValues() throws CouldNotPerformException, InterruptedException, TimeoutException, ExecutionException {
        if (!Registries.getUserRegistry().isDataAvailable()) {
            LOGGER.warn("!Registries.getUserRegistry().isDataAvailable()");
            return;
        }

        UserConfigType.UserConfig userConfig = Registries.getUserRegistry().getUserConfigById(userId.get()).getUserConfig();

        updateValues(userConfig);
    }

    private void updateValues(UserConfigType.UserConfig userConfig) throws InterruptedException, CouldNotPerformException, ExecutionException, TimeoutException {
        phone.setValue(getOrEmptyString(userConfig.getMobilePhoneNumber()));
        userName.setValue(getOrEmptyString(userConfig.getUserName()));
        mail.setValue(getOrEmptyString(userConfig.getEmail()));
        firstname.setValue(getOrEmptyString(userConfig.getFirstName()));
        lastName.setValue(getOrEmptyString(userConfig.getLastName()));
        occupant.setValue(userConfig.getOccupant());
        admin.setValue(CachedAuthenticationRemote.getRemote().isAdmin(userId.get()).get(2, TimeUnit.SECONDS));

        if (userId.get() != null) {
            groups.clear();
            groups.addAll(AuthorizationGroups.getGroupsByUser(userId.get()));
        }

    }


    private String getOrEmptyString(String string) {
        return string != null ? string : "";
    }

    public boolean isUnsaved() {
        return getUserId() == null || getUserId().isEmpty();
    }

    public String getUserId() {
        return userId.get();
    }

    public boolean hasUserId() {
        return userId.get() != null && !userId.get().isEmpty();
    }

    public String getPhone() {
        return phone.get();
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public String getUserName() {
        return userName.get();
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    public String getMail() {
        return mail.get();
    }

    public StringProperty mailProperty() {
        return mail;
    }

    public String getFirstname() {
        return firstname.get();
    }

    public StringProperty firstnameProperty() {
        return firstname;
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public boolean isOccupant() {
        return occupant.get();
    }

    public BooleanProperty occupantProperty() {
        return occupant;
    }

    public UserConfigType.UserConfig getUserConfig() {
        UserConfigType.UserConfig user = UserConfigType.UserConfig.newBuilder()
                .setUserName(getOrEmptyString(userName.get()))
                .setFirstName(getOrEmptyString(firstname.get()))
                .setLastName(getOrEmptyString(lastName.get()))
                .setEmail(getOrEmptyString(mail.get()))
                .setMobilePhoneNumber(getOrEmptyString(phone.get()))
                .setOccupant(occupant.get())
                .build();

        return user;
    }

    public boolean isAdmin() {
        return admin.get();
    }

    public BooleanProperty adminProperty() {
        return admin;
    }

    public List<UnitConfigType.UnitConfig> getGroups() {
        return groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserData that = (UserData) o;

        return Objects.equals(this.userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }

    public String getOriginalUserName() {
        return originalUserName;
    }
}
