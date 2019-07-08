package sample.controller;

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
import sample.model.Data;
import sample.model.MySQLConnector;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.*;
import java.util.ResourceBundle;

public class UpdateAppointmentController implements Initializable {

    @FXML
    public TextField titleTextBox;
    @FXML
    public TextField typeTextBox;
    @FXML
    public TextField contactTextBox;
    @FXML
    public DatePicker datePicker;
    @FXML
    public ChoiceBox<String> choiceBox;
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
    public Button updateAppointmentButton;
    @FXML
    public Button backToClientInfoButton;

    private int appointmentId;
    private int testId;
    public String titleText;
    public String typeText;
    LocalDate localDate;
    Timestamp oldStart;
    Timestamp oldEnd;
    LocalDateTime oldStartDateTime;
    LocalDateTime oldEndDateTime;

    CustomerInfoController customerInfoController = new CustomerInfoController();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //It should be noted that weekends are allowed by design since this company has sales people who work over the weekend.

        datePicker.setOnAction(e -> {
            localDate = datePicker.getValue();
        });
        choiceBox.getItems().addAll("08", "09", "10", "11", "12", "13", "14", "15", "16");
        durationSelectBox.getItems().addAll("15 Minutes", "30 Minutes", "45 Minutes", "1 Hour");
        choiceBox.setValue("08");
        durationSelectBox.setValue("15 Minutes");
    }

    public void backToCustInfo() throws Exception {
        Stage stage = (Stage) backToClientInfoButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/customerInfo.fxml"));
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

    public void saveGoToClientsList(Button button) throws Exception{
        System.out.println("right before closing");
        Stage stage = (Stage) button.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/customerInfo.fxml"));
        AnchorPane page = loader.load();
        CustomerInfoController controllerTest = loader.getController();
        controllerTest.setTextFields(Data.currentClient);
        controllerTest.customerAppointmentTableView.getItems().clear();
        controllerTest.customerAppointmentTableView.refresh();
        customerInfoController.setAppointmentData();
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
    }

    public void checkAppointments(LocalDateTime apptStart, LocalDateTime apptEnd, PreparedStatement preparedStatement, String type, String title, Button button) throws Exception {

        PreparedStatement ps = MySQLConnector.conn.prepareStatement("SELECT start, end FROM appointment WHERE customerId = ?");
        ps.setObject(1, Data.getClientId());
        ResultSet results = ps.executeQuery();

        if (!results.isBeforeFirst()) {
            preparedStatement.execute();
            PreparedStatement updateTable = MySQLConnector.conn.prepareStatement("SELECT appointmentId, description, start, end FROM appointment WHERE appointmentId = ?");
            updateTable.setObject(1, appointmentId);
            ResultSet aptResults = updateTable.executeQuery();

            while (aptResults.next()) {

                Timestamp start = aptResults.getTimestamp("start");
                Timestamp end = aptResults.getTimestamp("end");
                LocalDateTime ldtStart = start.toLocalDateTime();
                LocalDateTime ldtEnd = end.toLocalDateTime();
                ZoneId apptZone = ZoneId.systemDefault();
                ZonedDateTime startUTC = ZonedDateTime.of(ldtStart, apptZone);
                ZonedDateTime endUTC = ZonedDateTime.of(ldtEnd, apptZone);
                LocalDateTime newStart = startUTC.toLocalDateTime();
                LocalDateTime newEnd = startUTC.toLocalDateTime();
                String s = newStart.toString();
                String e = newEnd.toString();



                Appointment appointment = new Appointment(appointmentId, type, s, e, title);
                Data.acceptAppointment(appointment);
            }
            saveGoToClientsList(button);

        } else {
            Boolean checkOk = true;
            while (results.next()) {
                oldStart = results.getTimestamp("start");
                oldEnd = results.getTimestamp("end");
                oldStartDateTime = oldStart.toLocalDateTime();
                oldEndDateTime = oldEnd.toLocalDateTime();

                if (apptStart.isAfter(oldStartDateTime) && apptEnd.isBefore(oldEndDateTime)) {
                    checkOk = false;
                    break;
                    // Do these break statements get me out of the while statement?


                } else if (apptStart.isBefore(oldStartDateTime) && apptEnd.isAfter(oldStartDateTime) && apptEnd.isBefore(oldEndDateTime)) {
                    checkOk = false;
                    break;

                } else if (apptStart.isAfter(oldStartDateTime) && apptStart.isBefore(oldEndDateTime) && apptEnd.isAfter(oldEndDateTime)) {
                    checkOk = false;
                    break;

                } else if (apptStart.equals(oldStartDateTime) || apptEnd.equals(oldEndDateTime)){
                    checkOk = false;
                    break;
                }

                else {
                    checkOk = true;
                }
            }
            if(checkOk == true){
                preparedStatement.execute();
                PreparedStatement updateTable = MySQLConnector.conn.prepareStatement("SELECT appointmentId, start, end FROM appointment WHERE appointmentId = ?");
                updateTable.setObject(1, appointmentId);
                ResultSet aptResults = updateTable.executeQuery();
                while (aptResults.next()) {

                    Timestamp start = aptResults.getTimestamp("start");
                    Timestamp end = aptResults.getTimestamp("end");
                    LocalDateTime ldtStart = start.toLocalDateTime();
                    LocalDateTime ldtEnd = end.toLocalDateTime();
                    ZoneId apptZone = ZoneId.systemDefault();
                    ZonedDateTime startUTC = ZonedDateTime.of(ldtStart, apptZone);
                    ZonedDateTime endUTC = ZonedDateTime.of(ldtEnd, apptZone);
                    LocalDateTime newStart = startUTC.toLocalDateTime();
                    LocalDateTime newEnd = startUTC.toLocalDateTime();
                    String s = newStart.toString();
                    String e = newEnd.toString();

                    Appointment appointment = new Appointment(appointmentId, type, s, e, title);
                    Data.acceptAppointment(appointment);
                }
                saveGoToClientsList(button);

            }else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "This customer already has an appointment at this day and time. Please select a different appointment time");
                alert.showAndWait();
            }

        }
    }

    public void setUpdateTextFields(Appointment appointment) {
        try {
            typeText = appointment.getAppointmentType();
            appointmentId = appointment.getAppointmentId();
            titleText = appointment.getAppointmentTitle();
            titleTextBox.setText(titleText);
            typeTextBox.setText(typeText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAppointment() throws Exception {
        String stringMinute = "";
        String stringSecond = "00";
        String updateTitle = titleTextBox.getText();
        String updateType = typeTextBox.getText();

        try {

            String stringHour = choiceBox.getValue();
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
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please select a meeting time increment");
                alert.showAndWait();
            }

            int year = localDate.getYear();
            Month month = localDate.getMonth();
            int day = localDate.getDayOfMonth();
            int hour = Integer.parseInt(stringHour, 10);
            int minute = Integer.parseInt((stringMinute));
            int second = Integer.parseInt(stringSecond);
            int endHour;
            int endMinute = 0;
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

            LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
            LocalDateTime endDateTime = LocalDateTime.of(year, month, day, endHour, endMinute, second);

            ZoneId apptZone = ZoneId.systemDefault();

            ZonedDateTime startUTC = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));
            ZonedDateTime endUTC = ZonedDateTime.of(endDateTime, ZoneId.of("UTC"));

            LocalDateTime apptStart = startUTC.toLocalDateTime();
            LocalDateTime apptEnd = startUTC.toLocalDateTime();

            PreparedStatement statement = MySQLConnector.conn.prepareStatement("UPDATE appointment SET title = ?, description = ?, start = ?, end = ? WHERE appointmentId = ?");
            statement.setObject(1, updateTitle);
            statement.setObject(2, updateType);
            statement.setObject(3, apptStart);
            statement.setObject(4, apptEnd);
            statement.setObject(5, appointmentId);

            checkAppointments(apptStart, apptEnd, statement, updateType, updateTitle, updateAppointmentButton);

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "All values are required fields");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

}
