package sample.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Appointment {

    private IntegerProperty appointmentId;
    private StringProperty appointmentType;
    private StringProperty appointmentDate;
    private StringProperty appointmentTime;
    private StringProperty appointmentTitle;

    public Appointment(int appointmentId, String appointmentType,
                       String appointmentDate, String appointmentTime, String appointmentTitle) {
        this.appointmentId = new SimpleIntegerProperty(appointmentId);
        this.appointmentType = new SimpleStringProperty(appointmentType);
        this.appointmentDate = new SimpleStringProperty(appointmentDate);
        this.appointmentTime = new SimpleStringProperty(appointmentTime);
        this.appointmentTitle = new SimpleStringProperty(appointmentTitle);

    }

    public String getAppointmentTitle() {
        return appointmentTitle.get();
    }

    public StringProperty appointmentTitleProperty() {
        return appointmentTitle;
    }

    public void setAppointmentTitle(String appointmentTitle) {
        this.appointmentTitle.set(appointmentTitle);
    }

    public String getAppointmentDate() {
        return appointmentDate.get();
    }

    public StringProperty appointmentDateProperty() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate.set(appointmentDate);
    }

    public String getAppointmentTime() {
        return appointmentTime.get();
    }

    public StringProperty appointmentTimeProperty() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime.set(appointmentTime);
    }

    public int getAppointmentId() {
        return appointmentId.get();
    }

    public IntegerProperty appointmentIdProperty() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId.set(appointmentId);
    }

    public String getAppointmentType() {
        return appointmentType.get();
    }

    public StringProperty appointmentTypeProperty() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType.set(appointmentType);
    }

}
