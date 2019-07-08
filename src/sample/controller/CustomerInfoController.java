package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class CustomerInfoController implements Initializable {

    @FXML
    private TextField custNameText;
    @FXML
    private TextField custIdText;
    @FXML
    private TextField custAddressText;
    @FXML
    public TextField custPhoneText;
    @FXML
    private Button modCustInfoButton;
    @FXML
    private Button newAppointmentButton;
    @FXML
    public TableView<Appointment> customerAppointmentTableView;
    @FXML
    private Button cancelBackToCustListButton;
    @FXML
    private TableView<Appointment> viewTV;
    @FXML
    private TableColumn<Appointment, String> viewDate;
    @FXML
    private TableColumn<Appointment, String> viewPeriod;
    @FXML
    public Button monthViewButton;
    @FXML
    public Button setWeekViewButton;

    public int customerId;

    private int currentCustomerId;
    @FXML
    public Button deleteAppointmentButton;
    @FXML
    private TableColumn<Appointment, String> aptTypeColumn;
    @FXML
    private TableColumn<Appointment, String> aptDateColumn;
    @FXML
    private TableColumn<Appointment, String> aptTimeColumn;
    @FXML
    private Button updateExistingButton;

    public int testId;

    @FXML
    public void goToUpdateAppointment() throws Exception {
        Appointment appointmentSelected = customerAppointmentTableView.getSelectionModel().getSelectedItem();

        try {
            Stage stage = (Stage) updateExistingButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/updateAppointment.fxml"));
            AnchorPane page = loader.load();
            sample.controller.UpdateAppointmentController controllerTest = loader.getController();
            controllerTest.setUpdateTextFields(appointmentSelected);
            Scene scene = new Scene(page);
            Data.currentAppointment = appointmentSelected;
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You must select an appointment to view its info and update it");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void setAppointmentData() throws Exception {

        currentCustomerId = Data.getClientId();
        PreparedStatement ps = MySQLConnector.conn.prepareStatement("SELECT appointmentId, start, description, title FROM "
                + "appointment WHERE customerId = ?");
        //ps.setObject(1, currentCustomerId);
        ps.setObject(1, currentCustomerId);
        ResultSet combinedTables = ps.executeQuery();

        System.out.println(currentCustomerId);

        while (combinedTables.next()) {
            int aptId = combinedTables.getInt("appointmentId");
            String description = combinedTables.getString("description");

            Timestamp start = combinedTables.getTimestamp("start");
            LocalDateTime ldtStart = start.toLocalDateTime();
            ZoneId apptZone = ZoneId.systemDefault();
            ZonedDateTime startUTC = ZonedDateTime.of(ldtStart, apptZone);
            LocalDateTime newStart = startUTC.toLocalDateTime();
            String s = newStart.toString();
            String longDate = combinedTables.getString("start");
            String date = longDate.substring(0, 11);
            String title = combinedTables.getString("title");

            //String longStart = combinedTables.getString("start");
            String shortStart = s.substring(11);
            Appointment appointment = new Appointment(aptId, description, date, shortStart, title);
            Data.acceptAppointment(appointment);

        }
        ps.close();
        combinedTables.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.aptTypeColumn.setCellValueFactory((cellData) -> {
            return (cellData.getValue()).appointmentTypeProperty();
        });
        this.aptDateColumn.setCellValueFactory((cellData) -> {
            return (cellData.getValue()).appointmentDateProperty();
        });
        this.aptTimeColumn.setCellValueFactory((cellData) -> {
            return (cellData.getValue()).appointmentTimeProperty();
        });
        this.viewPeriod.setCellValueFactory((cellData) -> {
            return (cellData.getValue()).appointmentDateProperty();
        });
        this.viewDate.setCellValueFactory((cellData) -> {
            return (cellData.getValue()).appointmentDateProperty();
        });
        try {
            customerAppointmentTableView.setItems(Data.getAppointmentData());
            //viewTV.setItems(Data.getAppointmentData());
            //setTextFields(Data.currentClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMonthView(ActionEvent event) throws Exception{

        viewPeriod.setText("Month");
        viewDate.setText("Date");
        viewTV.getItems().clear();
        viewTV.refresh();
        PreparedStatement ps = MySQLConnector.conn.prepareStatement("SELECT start FROM appointment WHERE customerId = ?");
        ps.setObject(1, customerId);

        ResultSet results = ps.executeQuery();
        while(results.next()){
            Timestamp time = results.getTimestamp("start");
            LocalDateTime ldt = time.toLocalDateTime();
            String month = ldt.getMonth().toString();
            String start = ldt.toLocalDate().toString();
            Appointment appointment = new Appointment(0, "", month, start, "");
            Data.monthWeekList.add(appointment);
        }

        this.viewPeriod.setCellValueFactory((cellData) -> {
            return (cellData.getValue()).appointmentDateProperty();
        });
        this.viewDate.setCellValueFactory((cellData) -> {
            return (cellData.getValue()).appointmentTimeProperty();
        });

        viewTV.setItems(Data.getMonthWeekData());
    }

    public void setWeekView() throws Exception{
        viewPeriod.setText("Day");
        viewDate.setText("Time");
        viewTV.getItems().clear();
        viewTV.refresh();

        PreparedStatement ps = MySQLConnector.conn.prepareStatement("SELECT start FROM appointment WHERE customerId = ?");
        ps.setObject(1, customerId);

        ResultSet results = ps.executeQuery();
        while (results.next()) {
            viewTV.getItems().clear();
            viewTV.refresh();

            Timestamp time = results.getTimestamp("start");
            LocalDateTime ldt = time.toLocalDateTime();
            long difference = LocalDateTime.now().until(ldt, ChronoUnit.DAYS);
            String startTime = ldt.toLocalTime().toString();
            //String start = results.getString("start");
            String day = ldt.getDayOfWeek().toString();

            if((difference >= 0) && (difference <= 7)){
                Appointment appointment = new Appointment(0, "", day, startTime, "");
                Data.monthWeekList.add(appointment);
            }
        }

        this.viewPeriod.setCellValueFactory((cellData) -> {
            return (cellData.getValue()).appointmentDateProperty();
        });
        this.viewDate.setCellValueFactory((cellData) -> {
            return (cellData.getValue()).appointmentTimeProperty();
        });
        viewTV.setItems(Data.getMonthWeekData());
    }

    public void setTextFields(Client client) {
        try {
            String customerName = client.getClientName();
            customerId = client.getClientId();
            String customerPhone = client.getClientPhone();
            String customerAddress = client.getClientAddress();
            testId = client.getClientId();
            custNameText.setText(customerName);
            custIdText.setText(String.valueOf((customerId)));
            custAddressText.setText(customerAddress);
            custPhoneText.setText(customerPhone);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void backToClientList(ActionEvent event) throws Exception {
        goToClientList(cancelBackToCustListButton);
    }

    public void goToClientList(Button button) throws Exception {
        Stage stage = (Stage) button.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/customersList.fxml"));
        AnchorPane page = loader.load();
        sample.controller.CustomersListController controllerTest = loader.getController();
        controllerTest.clientsListTableView.getItems().clear();
        controllerTest.clientsListTableView.refresh();
        controllerTest.setClientData();
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();

    }

    public void modifyClientInfo(ActionEvent event) {

        try {
            int addressId = 0;
            PreparedStatement getAddId = MySQLConnector.conn.prepareStatement("SELECT addressId FROM customer WHERE customerId = ?");
            String custName = custNameText.getText();
            String custId = custIdText.getText();
            getAddId.setObject(1, custId);
            ResultSet results = getAddId.executeQuery();
            while(results.next()){
                addressId = results.getInt("addressId");
            }

            String custAddress = custAddressText.getText();
            String custPhone = custPhoneText.getText();
            PreparedStatement ps = MySQLConnector.conn.prepareStatement("UPDATE customer SET customerName = ?  WHERE customerId =" + custId);
            ps.setObject(1, custName);
            ps.executeUpdate();
            PreparedStatement updateAdd = MySQLConnector.conn.prepareStatement("UPDATE address SET address = ?, phone = ? WHERE addressId = ?");
            updateAdd.setObject(1, custAddress);
            updateAdd.setObject(2, custPhone);
            updateAdd.setObject(3, addressId);
            updateAdd.execute();

            goToClientList(modCustInfoButton);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void goToNewAppointment() throws Exception {

        Stage stage = (Stage) newAppointmentButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/newAppointment.fxml"));
        AnchorPane page = loader.load();
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
    }

    public void deleteSelected(ActionEvent event) {
        try {
            Appointment appointmentSelected = customerAppointmentTableView.getSelectionModel().getSelectedItem();
            Data.deleteAppointment(appointmentSelected);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("You must select an Appointment in the table to delete");
        }
    }
}
