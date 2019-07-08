package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.Main;
import sample.model.Appointment;
import sample.model.Client;
import sample.model.Data;
import sample.model.MySQLConnector;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class CustomersListController implements Initializable {

    @FXML
    private Button randomReport;
    @FXML
    public TableView<Client> clientsListTableView;
    @FXML
    private TableColumn<Client, String> nameColumn;
    @FXML
    private TableColumn<Client, String> phoneColumn;
    @FXML
    private TableColumn<Client, String> addressColumn;
    @FXML
    private TableColumn<Client, Number> custIdColumn;
    @FXML
    private Button proceedButton;
    @FXML
    private Button exitCustListButton;
    @FXML
    private Button backToLoginButton;
    @FXML
    private Button deleteClientButton;
    @FXML
    private Button goToAddClientButton;
    @FXML
    private ChoiceBox aptByMonthReport;
    @FXML
    private Label aptsByMonth;
    @FXML
    private Button aptTypeByMonthButton;
    @FXML
    private ChoiceBox consultScheduleCB;
    @FXML
    private Button goToSchedButton;
    @FXML
    private Label clientCountLabel;

    private int userId;

    CustomerInfoController custInfo = new CustomerInfoController();

    public void generateReportButton(ActionEvent event) throws Exception {
        PreparedStatement ps = MySQLConnector.conn.prepareStatement("SELECT DISTINCT COUNT(customerId) FROM customer");
        ResultSet results = ps.executeQuery();
        while (results.next()) {
            int count = results.getInt(1);
            clientCountLabel.setText(Integer.toString(count));
        }
    }

    public void upcomingApt() {
        try {
            PreparedStatement ps = MySQLConnector.conn.prepareStatement("SELECT start FROM appointment");
            LocalDateTime currentTime = LocalDateTime.now();
            ResultSet results = ps.executeQuery();

            while (results.next()) {

                ZoneId apptZone = ZoneId.systemDefault();
                LocalDateTime getAppointment = results.getTimestamp("start").toLocalDateTime();
                ZonedDateTime startUTC = ZonedDateTime.of(getAppointment, apptZone);
                long diffInMinutes = ChronoUnit.MINUTES.between(currentTime, startUTC);

                if ((startUTC.getYear() == currentTime.getYear()) && (startUTC.getMonth() == currentTime.getMonth()) &&
                        (startUTC.getDayOfMonth() == currentTime.getDayOfMonth()) && (diffInMinutes >= 0 && diffInMinutes <= 15)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "You have an appointment starting in the next 15 minutes!");
                    alert.showAndWait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setClientData() throws Exception {

        Statement statement = MySQLConnector.conn.createStatement();
        ResultSet combinedTables = statement.executeQuery("SELECT customer.customerId, customer.customerName, address.address, address.phone FROM "
                + "customer INNER JOIN address ON customer.addressId = address.addressId");

        while (combinedTables.next()) {
            int customerId = combinedTables.getInt("customerId");
            String customerName = combinedTables.getString("customerName");
            String address = combinedTables.getString("address");
            String phone = combinedTables.getString("phone");
            Client client = new Client(customerId, customerName, phone, address);
            Data.acceptClient(client);

        }
        statement.close();
        combinedTables.close();
    }

    public void setBackToLoginButton() {
        try {

            Stage stage = (Stage) backToLoginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/loginPage.fxml"));
            AnchorPane page = loader.load();
            MySQLConnector.conn.close();
            Scene scene = new Scene(page);
            try {
                clientsListTableView.getItems().clear();
                clientsListTableView.refresh();
                System.out.println("made it into the try statement");
            } catch (Exception e) {
                System.out.println("wasn't there");
            }
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        upcomingApt();

        this.nameColumn.setCellValueFactory((cellData) -> {
            return (cellData.getValue()).clientNameProperty();
        });
        this.phoneColumn.setCellValueFactory((cellData) -> {
            return (cellData.getValue()).clientPhoneProperty();
        });
        this.addressColumn.setCellValueFactory((cellData) -> {
            return (cellData.getValue()).clientAddressProperty();
        });
        this.custIdColumn.setCellValueFactory((cellData) -> {
            return (cellData.getValue()).clientIdProperty();
        });
        try {
            clientsListTableView.setItems(Data.getClientData());
        } catch (Exception e) {
            e.printStackTrace();
        }
        aptByMonthReport.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        aptByMonthReport.setValue("January");
        consultScheduleCB.getItems().addAll("Chris", "Ryan");
        consultScheduleCB.setValue("Chris");
    }

    public void selectUser() throws Exception {
        if (consultScheduleCB.getValue().equals("Chris")) {
            userId = 1;
            Data.selectedUser = "Chris";
        } else {
            userId = 12;
            Data.selectedUser = "Ryan";
        }
        PreparedStatement ps = MySQLConnector.conn.prepareStatement("SELECT * FROM appointment WHERE userId = ?");
        ps.setObject(1, userId);

        ResultSet results = ps.executeQuery();

        while (results.next()) {
            String type = results.getString("description");
            String longDate = results.getString("start");
            String date = longDate.substring(0, 11);
            String longTime = results.getString("start");
            String time = longTime.substring(12);
            String title = results.getString("title");
            Appointment appointment = new Appointment(1, type, date, time, title);
            Data.acceptConsultAppt(appointment);
        }
    }

    public void goToSchedViewer() throws Exception {

        selectUser();
        try {

            Stage stage = (Stage) goToSchedButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/scheduleViewer.fxml"));
            AnchorPane page = loader.load();
            Scene scene = new Scene(page);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.out.println("You are throwing this error:");
            e.printStackTrace();
        }
    }

    public void getAptByMonth() throws Exception {
        int month;
        if (aptByMonthReport.getValue().equals("January")) {
            month = 01;
            System.out.println("January worked");
        } else if (aptByMonthReport.getValue().equals("February")) {
            month = 02;
        } else if (aptByMonthReport.getValue().equals("March")) {
            month = 03;
        } else if (aptByMonthReport.getValue().equals("April")) {
            month = 04;
        } else if (aptByMonthReport.getValue().equals("May")) {
            month = 05;
        } else if (aptByMonthReport.getValue().equals("June")) {
            month = 06;
        } else if (aptByMonthReport.getValue().equals("July")) {
            month = 07;
        } else if (aptByMonthReport.getValue().equals("August")) {
            month = 8;
        } else if (aptByMonthReport.getValue().equals("September")) {
            month = 9;
        } else if (aptByMonthReport.getValue().equals("October")) {
            month = 10;
        } else if (aptByMonthReport.getValue().equals("November")) {
            month = 11;
        } else {
            month = 12;

        }

        PreparedStatement statement = MySQLConnector.conn.prepareStatement("SELECT COUNT(*) AS total FROM appointment WHERE MONTH(start) = ?");
        statement.setObject(1, month);
        ResultSet results = statement.executeQuery();

        while (results.next()) {
            int count = results.getInt(1);
            aptsByMonth.setText(Integer.toString(count));
        }
    }

    @FXML
    public void exitCustomersList() throws Exception {
        Stage stage = (Stage) exitCustListButton.getScene().getWindow();
        MySQLConnector.conn.close();
        stage.close();
    }

    public void goToCustomerInfo(ActionEvent event) {
        Client clientSelected = clientsListTableView.getSelectionModel().getSelectedItem();

        try {

            Stage stage = (Stage) proceedButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/customerInfo.fxml"));
            AnchorPane page = loader.load();
            CustomerInfoController controllerTest = loader.getController();
            controllerTest.setTextFields(clientSelected);
            Scene scene = new Scene(page);
            controllerTest.customerAppointmentTableView.getItems().clear();
            controllerTest.customerAppointmentTableView.refresh();
            Data.currentClient = clientSelected;
            custInfo.setAppointmentData();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You must select a client to view their info");
            alert.showAndWait();
        }
    }

    public void goToAddClient() throws Exception {
        Stage stage = (Stage) goToAddClientButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/addClient.fxml"));
        AnchorPane page = loader.load();
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void deleteSelected(ActionEvent event) {
        try {
            Client clientSelected = clientsListTableView.getSelectionModel().getSelectedItem();
            Data.deleteClient(clientSelected);

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You must select a customer's info to delete");
            alert.showAndWait();
        }
    }
}
