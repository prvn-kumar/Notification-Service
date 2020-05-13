package com.payon.webhook.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class WebhookException extends RuntimeException {

    public WebhookException() {
    }

    public WebhookException(String message) {
        super(message);
    }
}
