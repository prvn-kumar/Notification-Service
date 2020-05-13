package com.payon.webhook.model;

public class DecryptedNotificationDto extends WebHookNotification {

    private String decryptedContent;

    public String getDecryptedContent() {
        return decryptedContent;
    }

    public void setDecryptedContent(String decryptedContent) {
        this.decryptedContent = decryptedContent;
    }
}
