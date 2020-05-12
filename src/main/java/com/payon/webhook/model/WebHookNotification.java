package com.payon.webhook.model;

import javax.persistence.*;

@Entity
public class WebHookNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(columnDefinition="LONGTEXT")
    private String encryptedBody;
    private String ivHeader;
    private String authHeader;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
