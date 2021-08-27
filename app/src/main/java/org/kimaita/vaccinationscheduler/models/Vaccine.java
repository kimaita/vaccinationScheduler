package org.kimaita.vaccinationscheduler.models;

public class Vaccine {

    int vaccineDBID;
    String vaccineName;
    String vaccineAdministration;
    String vaccineLink;
    String vaccineDates;
    String vaccineDiseases;

    public String getVaccineLink() {
        return vaccineLink;
    }

    public void setVaccineLink(String vaccineLink) {
        this.vaccineLink = vaccineLink;
    }

    public String getVaccineDates() {
        return vaccineDates;
    }

    public void setVaccineDates(String vaccineDates) {
        this.vaccineDates = vaccineDates;
    }

    public String getVaccineDiseases() {
        return vaccineDiseases;
    }

    public void setVaccineDiseases(String vaccineDiseases) {
        this.vaccineDiseases = vaccineDiseases;
    }

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
