package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sample.model.Data;
import sample.model.MySQLConnector;

import java.net.URL;
import java.sql.PreparedStatement;
import java.time.*;
import java.util.ResourceBundle;

public class NewAppointmentController implements Initializable {

    @FXML
    private Button saveNewAppointmentButton;
    @FXML
    private TextField newAppointmentTitleText;

    @FXML
    private TextField newAppointmentTypeText;
    @FXML
    private TextField newAppointmentContactText;
    @FXML
    private DatePicker newAppointmentDatePicker;
    @FXML
    private LocalDate localDate;
    @FXML
    private ChoiceBox<String> timeSelectBox;
    @FXML
    private RadioButton rbZero;
    @FXML
    private RadioButton rbFifteen;
    @FXML
    private RadioButton rbThirty;
    @FXML
    private RadioButton rbFortyFive;
    @FXML
    private ChoiceBox<String> durationSelectBox;
    @FXML
    private Button backToClientInfoButton;

    private String stringMinute = "";
    private String stringSecond = "00";
    private int hour;
    private int minute;
    private int second;
    private int endHour;
    private int endMinute;
    String customerId = "1";
    CustomersListController test = new CustomersListController();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        newAppointmentDatePicker.setOnAction(e -> {
            localDate = newAppointmentDatePicker.getValue();
        });

        timeSelectBox.getItems().addAll("08", "09", "10", "11", "12", "13", "14", "15", "16");
        timeSelectBox.setValue("08");
        durationSelectBox.getItems().addAll("15 Minutes", "30 Minutes", "45 Minutes", "1 Hour");
        durationSelectBox.setValue("15 Minutes");
        setCustomerId();
        //setCustomerId();
    }

    public void setCustomerId() {
        CustomersListController getId = new CustomersListController();
        customerId = Integer.toString(Data.currentClient.getClientId());
        System.out.println("my customer id: " + customerId);

    }

    public void newAppointmentSubmit() throws Exception {

        String title = newAppointmentTitleText.getText();
        String type = newAppointmentTypeText.getText();

        String stringHour = timeSelectBox.getValue();

        try {
            if (rbZero.isSelected()) {
                stringMinute = "00";
            } else if (rbFifteen.isSelected()) {
                stringMinute = "15";
            } else if (rbThirty.isSelected()) {
                stringMinute = "30";
            } else if (rbFortyFive.isSelected()) {
                stringMinute = "45";
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please select a radiobox value for minute");
            alert.showAndWait();
            e.printStackTrace();
        }

        int year = localDate.getYear();
        Month month = localDate.getMonth();
        int day = localDate.getDayOfMonth();
        hour = Integer.parseInt(stringHour);
        minute = Integer.parseInt((stringMinute));
        second = Integer.parseInt(stringSecond);

        int duration = -1;

        try {
            if (durationSelectBox.getValue().equals("15 Minutes")) {
                duration = 15;
            } else if (durationSelectBox.getValue().equals("30 Minutes")) {
                duration = 30;
            } else if (durationSelectBox.getValue().equals("45 Minutes")) {
                duration = 45;
            } else if (durationSelectBox.getValue().equals("1 Hour")) {
                duration = 1;
            } else {
                duration = -1;
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You must select a duration");
            alert.showAndWait();
        }

        if ((minute == 15 || minute == 30) && duration == 15) {
            endMinute = duration + minute;
            endHour = hour;
        } else if (minute == 0 && (duration == 15)) {
            endMinute = duration + minute;
            endHour = hour;
        } else if (minute == 0 && (duration == 45)) {
            endMinute = duration;
            endHour = hour;
        } else if (minute == 45 && duration == 15) {
            endHour = hour + 1;
        } else if (minute == 45 && duration == 30) {
            endMinute = 15;
            endHour = hour + 1;
        } else if (minute == 45 && duration == 45) {
            endMinute = 30;
            endHour = hour + 1;
        } else if (minute == 45 && duration == 1) {
            endMinute = minute;
            endHour = hour + 1;
        } else if (minute == 0 && duration == 30) {
            endMinute = 30;
            endHour = hour;
        } else if (minute == 0 && duration == 1) {
            endMinute = 0;
            endHour = hour + 1;
        } else if (minute == 30 && duration == 30) {
            endMinute = 0;
            endHour = hour + 1;
        } else if (minute == 30 && duration == 45) {
            endMinute = 15;
            endHour = hour + 1;
        } else if (minute == 30 && duration == 1) {
            endMinute = 30;
            endHour = hour + 1;
        } else if (minute == 15 && duration == 45) {
            endMinute = 0;
            endHour = hour + 1;
        } else {
            endMinute = 0;
            endHour = hour + 1;
        }

        //gets string and integer values from the drop town menus, put them together to make a LocalDateTime
        LocalDateTime endDateTime = LocalDateTime.of(year, month, day, endHour, endMinute, second);
        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute, second);

        //gets the timezone of the user's system, converts the value to a ZonedDateTime
        ZoneId apptZone = ZoneId.systemDefault();
        ZonedDateTime startUTC = ZonedDateTime.of(localDateTime, apptZone);
        ZonedDateTime endUTC = ZonedDateTime.of(endDateTime, apptZone);

        LocalDateTime apptStart = startUTC.toLocalDateTime();
        LocalDateTime apptEnd = endUTC.toLocalDateTime();

        PreparedStatement ps = MySQLConnector.conn.prepareStatement("INSERT INTO appointment (customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy, userId)"
                + "VALUES (?, ?, ?, 'location', '555 5555', 'url', ?, ?, CURRENT_DATE , 'Chris', CURRENT_TIMESTAMP , 'Chris', 1)");

        ps.setObject(1, customerId);
        ps.setObject(2, title);
        ps.setObject(3, type);
        ps.setObject(4, apptStart);
        ps.setObject(5, apptEnd);

        sample.controller.UpdateAppointmentController updateApp = new sample.controller.UpdateAppointmentController();
        updateApp.checkAppointments(apptStart, apptEnd, ps, type, title, saveNewAppointmentButton);

    }

    public void backToClientInfo() throws Exception {
        Stage stage = (Stage) backToClientInfoButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/customerInfo.fxml"));
        Parent root = loader.load();

        try {
            CustomerInfoController controllerTest = loader.getController();
            controllerTest.setTextFields(Data.currentClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
