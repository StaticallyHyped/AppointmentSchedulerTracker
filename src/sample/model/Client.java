package sample.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Client {

    private IntegerProperty clientId;
    private StringProperty clientName;
    private StringProperty clientPhone;
    private StringProperty clientAddress;

    public Client(int clientId, String clientName, String clientPhone, String clientAddress) {
        this.clientId = new SimpleIntegerProperty(clientId);
        this.clientName = new SimpleStringProperty(clientName);
        this.clientAddress = new SimpleStringProperty(clientAddress);
        this.clientPhone = new SimpleStringProperty(clientPhone);
    }

    public int getClientId() {
        return clientId.get();
    }

    public IntegerProperty clientIdProperty() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId.set(clientId);
    }

    public String getClientName() {
        return clientName.get();
    }

    public StringProperty clientNameProperty() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName.set(clientName);
    }

    public String getClientPhone() {
        return clientPhone.get();
    }

    public StringProperty clientPhoneProperty() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone.set(clientPhone);
    }

    public String getClientAddress() {
        return clientAddress.get();
    }

    public StringProperty clientAddressProperty() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress.set(clientAddress);
    }
}
