package org.kimaita.vaccinationscheduler.models;

import java.sql.Date;

public class Child {

    String childName;
    Date childDoB;

    public void setChildDoB(Date childDoB) {
        this.childDoB = childDoB;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public Date getChildDoB() {
        return childDoB;
    }

    public String getChildName() {
        return childName;
    }
}
