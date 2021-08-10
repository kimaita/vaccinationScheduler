package org.kimaita.vaccinationscheduler.models;

public class Message {

    int id, sender, hospital, parent;
    String content, hospitalName;
    long time;
    boolean read;

    public void setId(int id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public void setHospital(int hospital) {
        this.hospital = hospital;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public int getParent() {
        return parent;
    }

    public int getId() {
        return id;
    }

    public int getSender() {
        return sender;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public int getHospital() {
        return hospital;
    }

    public boolean isRead() {
        return read;
    }

    public long getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }
}
