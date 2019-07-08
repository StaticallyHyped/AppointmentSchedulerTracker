package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import sample.model.Client;
import sample.model.Data;
import sample.model.MySQLConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AddClientController {

    @FXML
    private Button submitAddClient;
    @FXML
    private Button cancelAddClient;
    @FXML
    private Button cancelBackToCustListButton;
    @FXML
    private TextField clientNameText;
    @FXML
    private TextField clientAddressText;
    @FXML
    private TextField clientPhoneText;

    sample.controller.CustomerInfoController customerInfoController = new sample.controller.CustomerInfoController();
    //CustomersListController test = new CustomersListController();

    public void addClient() throws Exception {
        //TODO add try with resources
        Statement statement = MySQLConnector.conn.createStatement();
        String clientName = clientNameText.getText();
        String clientAddress = clientAddressText.getText();
        String clientPhone = clientPhoneText.getText();
        int addressId;

        PreparedStatement addAddress = MySQLConnector.conn.prepareStatement(("INSERT INTO address(address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy)"
                + " VALUES (?, 'not needed', 1,'97707', ?, CURRENT_DATE , 'Chris', CURRENT_TIMESTAMP , 'Chris')"));
        addAddress.setObject(1, clientAddress);
        addAddress.setObject(2, clientPhone);
        addAddress.executeUpdate();

        PreparedStatement getAddress = MySQLConnector.conn.prepareStatement("SELECT addressId FROM address WHERE address = ?");
        getAddress.setObject(1, clientAddress);
        ResultSet results = getAddress.executeQuery();
        PreparedStatement ps = MySQLConnector.conn.prepareStatement("INSERT INTO customer(customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) "
                + "VALUES (?, ?, 1,CURRENT_DATE , 'Chris', CURRENT_TIMESTAMP , 'Chris')");
        PreparedStatement getId = MySQLConnector.conn.prepareStatement("SELECT customerId FROM customer WHERE addressId = ?");

        while (results.next()) {
            addressId = results.getInt(1);
            ps.setObject(2, addressId);
            getId.setObject(1, addressId);
        }
        ps.setObject(1, clientName);
        ps.executeUpdate();

        ResultSet idResult = getId.executeQuery();
        while (idResult.next()) {
            int clientId = idResult.getInt(1);
            Client client = new Client(clientId, clientName, clientPhone, clientAddress);
            Data.acceptClient(client);
        }

        statement.close();

        //method that changes scenes to client list
        customerInfoController.goToClientList(submitAddClient);
    }

    public void cancelBackToCustInfo() throws Exception {

        //method that changes scenes to client list
        customerInfoController.goToClientList(cancelAddClient);
    }

    public void cancelBackToCustList() throws Exception {
        customerInfoController.goToClientList(cancelBackToCustListButton);
    }

}
