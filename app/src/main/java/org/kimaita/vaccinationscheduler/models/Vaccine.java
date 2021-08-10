package org.kimaita.vaccinationscheduler.models;

public class Vaccine {

    int vaccineDBID;
    String vaccineName;
    String vaccineAdministration;

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public void setVaccineAdministration(String vaccineAdministration) {
        this.vaccineAdministration = vaccineAdministration;
    }

    public void setVaccineDBID(int vaccineDBID) {
        this.vaccineDBID = vaccineDBID;
    }

    public int getVaccineDBID() {
        return vaccineDBID;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public String getVaccineAdministration() {
        return vaccineAdministration;
    }
}
