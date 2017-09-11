package org.openbase.bco.bcozy.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.pattern.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.registry.UserRegistryDataType;
import rst.domotic.unit.UnitConfigType;
import rst.domotic.unit.user.UserConfigType;

/**
 * @author vdasilva
 */
public class UserData {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserData.class);

    private final StringProperty userId = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty userName = new SimpleStringProperty();
    private final StringProperty mail = new SimpleStringProperty();
    private final StringProperty firstname = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final BooleanProperty occupant = new SimpleBooleanProperty();
    //BooleanProperty admin;


    private final Observer<UserRegistryDataType.UserRegistryData> userRegistryDataObserver = (source, data) -> updateValues();

    public static UserData currentUser() throws CouldNotPerformException, InterruptedException {
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

    public UserData(String userId) throws CouldNotPerformException, InterruptedException {
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

    public UserData(UnitConfigType.UnitConfig unitConfig) throws CouldNotPerformException, InterruptedException {
        userId.setValue(unitConfig.getId());

        updateValues(unitConfig.getUserConfig());
        Registries.getUserRegistry().addDataObserver(userRegistryDataObserver);
    }

    private void updateValues() throws CouldNotPerformException, InterruptedException {
        if (!Registries.getUserRegistry().isDataAvailable()) {
            LOGGER.warn("!Registries.getUserRegistry().isDataAvailable()");
            return;
        }

        UserConfigType.UserConfig userConfig = Registries.getUserRegistry().getUserConfigById(userId.get()).getUserConfig();

        updateValues(userConfig);
    }

    private void updateValues(UserConfigType.UserConfig userConfig) {
        phone.setValue(userConfig.getMobilePhoneNumber());
        userName.setValue(userConfig.getUserName());
        mail.setValue(userConfig.getEmail());
        firstname.setValue(userConfig.getFirstName());
        lastName.setValue(userConfig.getLastName());
        occupant.setValue(userConfig.getOccupant());
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
                .setUserName(userName.get())
                .setFirstName(firstname.get())
                .setLastName(lastName.get())
                .setEmail(mail.get())
                .setMobilePhoneNumber(phone.get())
                .setOccupant(occupant.get())
                .build();

        return user;
    }
}
