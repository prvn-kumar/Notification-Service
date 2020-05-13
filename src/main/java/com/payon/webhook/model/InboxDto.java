package com.payon.webhook.model;

public class InboxDto {
    private String name;
    private String location;
    private int notificationCount;
    private boolean configKeySaved;

    public InboxDto() {
    }

    public InboxDto(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public int getNotificationCount() {
        return notificationCount;
    }

    public boolean isConfigKeySaved() {
        return configKeySaved;
    }

    public void setConfigKeySaved(boolean configKeySaved) {
        this.configKeySaved = configKeySaved;
    }

    public void setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
