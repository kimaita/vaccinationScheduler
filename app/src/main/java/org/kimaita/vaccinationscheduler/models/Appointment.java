package org.kimaita.vaccinationscheduler.models;

import java.sql.Date;

public class Appointment {
    int childID, appointmentID;
    Date vaccinationDate;
    boolean administered;
    int vaccine;
    String vaccineName;

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public void setChildID(int childID) {
        this.childID = childID;
    }

    public void setVaccinationDate(Date vaccinationDate) {
        this.vaccinationDate = vaccinationDate;
    }

    public void setVaccine(int vaccine) {
        this.vaccine = vaccine;
    }

    public void setAdministered(boolean administered) {
        this.administered = administered;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public int getVaccine() {
        return vaccine;
    }

    public int getAppointmentID() {
        return appointmentID;
    }

    public boolean isAdministered() {
        return administered;
    }

    public Date getVaccinationDate() {
        return vaccinationDate;
    }

    public int getChildID() {
        return childID;
    }
}
