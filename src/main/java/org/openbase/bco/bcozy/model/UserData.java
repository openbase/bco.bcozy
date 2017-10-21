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
import org.openbase.jul.pattern.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.registry.UserRegistryDataType;
import rst.domotic.unit.UnitConfigType;
import rst.domotic.unit.user.UserConfigType;

import java.util.ArrayList;
import java.util.List;
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
    private final StringProperty userName = new SimpleStringProperty("");
    private final StringProperty mail = new SimpleStringProperty("");
    private final StringProperty firstname = new SimpleStringProperty("");
    private final StringProperty lastName = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final BooleanProperty occupant = new SimpleBooleanProperty(false);
    private final BooleanProperty admin = new SimpleBooleanProperty(false);
    private final List<UnitConfigType.UnitConfig> groups = new ArrayList<>();

    //BooleanProperty admin;


    private final Observer<UserRegistryDataType.UserRegistryData> userRegistryDataObserver = (source, data) -> updateValues();

    public static UserData currentUser() throws CouldNotPerformException, InterruptedException, TimeoutException, ExecutionException {
        return new UserData(SessionManager.getInstance().getUserId());
    }

    /**
     * UserData for non-existing-user.
     *
     * @throws CouldNotPerformException
     * @throws InterruptedException
     */
    public UserData() {
        userId.setValue(null);
    }

    public UserData(String userId) throws CouldNotPerformException, InterruptedException, TimeoutException, ExecutionException {
        this.userId.setValue(userId);

//        mock();//FIXME

        updateValues();
        Registries.getUserRegistry().addDataObserver(userRegistryDataObserver);
    }

    private void mock() {
        phone.set("0123-456789");
        userName.set("Nutzername");
        firstname.set("Firstname");
        mail.set("mail@example.com");
        lastName.set("Lastname");
        occupant.set(false);
        password.set("********");
    }

    public UserData(UnitConfigType.UnitConfig unitConfig) throws CouldNotPerformException, InterruptedException, TimeoutException, ExecutionException {
        userId.setValue(unitConfig.getId());

        updateValues(unitConfig.getUserConfig());
        Registries.getUserRegistry().addDataObserver(userRegistryDataObserver);
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

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
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

        UserData userData = (UserData) o;

        return userId != null ? userId.equals(userData.userId) : userData.userId == null;
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }
}
