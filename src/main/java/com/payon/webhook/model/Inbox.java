package com.payon.webhook.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Inbox {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String configurationKey;
    @OneToMany(cascade = {CascadeType.ALL})
    private List<WebHookNotification> notifications;

    public Inbox() {
    }

    public Inbox(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<WebHookNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<WebHookNotification> notifications) {
        this.notifications = notifications;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfigurationKey() {
        return configurationKey;
    }

    public void setConfigurationKey(String configurationKey) {
        this.configurationKey = configurationKey;
    }
}
