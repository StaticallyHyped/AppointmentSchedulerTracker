package sample.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Data {

    //creates the data file/list
    public static ObservableList<Client> clientData = FXCollections.observableArrayList();

    //a static method that is run in the Connector class - this adds client objects to the array list
    public static void acceptClient(Client client) {
        clientData.add(client);
    }

    //this returns the arrayList so I can call it from controller class
    public static ObservableList<Client> getClientData() {
        return clientData;
    }
    // static method that will be called from my Connector class

    public static void deleteClient(Client client) throws Exception {
        clientData.remove(client);
        int clientId = client.getClientId();
        System.out.println("This is the client ID: " + clientId);
        PreparedStatement ps = MySQLConnector.conn.prepareStatement("DELETE FROM customer WHERE customerId = ?");
        ps.setObject(1, clientId);
        ps.executeUpdate();
    }

    public static Client currentClient;

    public static ObservableList<Appointment> appointmentData = FXCollections.observableArrayList();

    public static void acceptAppointment(Appointment appointment) {
        appointmentData.add(appointment);
    }

    public static ObservableList<Appointment> getAppointmentData() {
        return appointmentData;
    }

    public static ObservableList<Appointment> monthWeekList = FXCollections.observableArrayList();

    public static ObservableList<Appointment> getMonthWeekData(){
        return monthWeekList;
    }
    public static void acceptMonthWeekAppointment(Appointment appointment){
        monthWeekList.add(appointment);
    }

    public static void deleteAppointment(Appointment appointment) throws Exception {
        appointmentData.remove(appointment);
        int appointmentId = appointment.getAppointmentId();

        PreparedStatement ps = MySQLConnector.conn.prepareStatement("DELETE FROM appointment WHERE appointmentId = ?");
        ps.setObject(1, appointmentId);
        ps.executeUpdate();

    }

    public static int getClientId() {
        return currentClient.getClientId();

    }

    public static ObservableList<Appointment> consultSchedule = FXCollections.observableArrayList();

    public static void acceptConsultAppt(Appointment appointment) {
        consultSchedule.add(appointment);
    }

    public static ObservableList<Appointment> getConsulAppointmentData() {
        return consultSchedule;
    }
    public static String selectedUser;
    public static Appointment currentAppointment;

}

