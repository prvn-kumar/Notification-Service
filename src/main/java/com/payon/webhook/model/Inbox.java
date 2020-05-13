package com.payon.webhook.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Inbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    @CreationTimestamp
    private LocalDateTime createdTime;

    private String configurationKey;

    @OneToMany(cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY)
    private List<WebHookNotification> notifications;

    public Inbox() {
        this.notifications = new ArrayList<>();
    }

    public Inbox(String name) {
        this.name = name;
        this.notifications = new ArrayList<>();
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
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
