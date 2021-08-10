package org.kimaita.vaccinationscheduler.models;

import java.sql.Date;

public class Child {

    int childDBID;
    String childName;
    Date childDoB;

    public void setChildDoB(Date childDoB) {
        this.childDoB = childDoB;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public void setChildDBID(int childDBID) {
        this.childDBID = childDBID;
    }

    public int getChildDBID() {
        return childDBID;
    }

    public Date getChildDoB() {
        return childDoB;
    }

    public String getChildName() {
        return childName;
    }
}
