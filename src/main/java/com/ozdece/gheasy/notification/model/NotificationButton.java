package com.ozdece.gheasy.notification.model;

public class NotificationButton {

    private final String id;
    private final String text;
    private Runnable action;

    public NotificationButton(String id, String text, Runnable action) {
        this.id = id.trim().replace(" ", "_");
        this.text = text;
        this.action = action;
    }

    public String getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public void fireButtonClick() {
       this.action.run();
    }

}
