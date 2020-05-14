package com.payon.webhook.model;

import com.payon.webhook.domain.WebHookNotification;

public class DecryptedNotificationDto extends WebHookNotification {

    private Notification decryptedContent;

    public Notification getDecryptedContent() {
        return decryptedContent;
    }

    public void setDecryptedContent(Notification decryptedContent) {
        this.decryptedContent = decryptedContent;
    }
}