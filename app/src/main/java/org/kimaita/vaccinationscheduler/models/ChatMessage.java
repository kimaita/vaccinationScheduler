package org.kimaita.vaccinationscheduler.models;

public abstract class ChatMessage {

    public static final int TYPE_DATE = 0;
    public static final int TYPE_CONTENT = 1;

    abstract public int getType();

}
