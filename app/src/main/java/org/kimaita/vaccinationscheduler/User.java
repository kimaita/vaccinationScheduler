package org.kimaita.vaccinationscheduler;

public class User {

    String username;
    String email;
    String phoneNumber;
    int pin;
    int natID;

    public User(int natID, String username, String email, String phoneNumber, int pin){

        this.natID = natID;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.pin = pin;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNatID(int natID) {
        this.natID = natID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getNatID() {
        return natID;
    }

    public int getPin() {
        return pin;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
