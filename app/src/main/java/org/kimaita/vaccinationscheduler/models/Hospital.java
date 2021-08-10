package org.kimaita.vaccinationscheduler.models;

public class Hospital {

    String hospital_name;
    int hospital_id;
    String email_address;
    double longitude, latitude;

    public void setHospital_id(int hospital_id) {
        this.hospital_id = hospital_id;
    }

    public void setHospital_name(String hospital_name) {
        this.hospital_name = hospital_name;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getHospital_id() {
        return hospital_id;
    }

    public String getHospital_name() {
        return hospital_name;
    }

    public String getEmail_address() {
        return email_address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
