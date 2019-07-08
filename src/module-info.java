module AppointmentSchedulerTracker {

    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;

    opens sample;
    opens sample.controller;
    opens sample.model;
    opens sample.view;
}