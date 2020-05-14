package com.payon.webhook.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class WebHookNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @CreationTimestamp
    private LocalDateTime createdTime;

    @Column(columnDefinition = "LONGTEXT")
    private String encryptedBody;
    private String ivHeader;
    private String authHeader;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getEncryptedBody() {
        return encryptedBody;
    }

    public void setEncryptedBody(String encryptedBody) {
        this.encryptedBody = encryptedBody;
    }

    public String getIvHeader() {
        return ivHeader;
    }

    public void setIvHeader(String ivHeader) {
        this.ivHeader = ivHeader;
    }

    public String getAuthHeader() {
        return authHeader;
    }

    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }
}
