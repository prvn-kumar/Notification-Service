package com.payon.webhook.service;

import org.springframework.stereotype.Service;

@Service
public class InboxService {

    public String createInbox() {
        return getAlphaNumericString(8);
    }

    private String getAlphaNumericString(int inboxIdLength) {

        // chose a Character random from this String
        String alphaNumericString = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789abcdefghjkmnpqrstuvxyz";

        // create StringBuffer size of alphaNumericString
        StringBuilder sb = new StringBuilder(inboxIdLength);

        for (int i = 0; i < inboxIdLength; i++) {
            // generate a random number between
            // 0 to alphaNumericString variable length
            int index = (int) (alphaNumericString.length() * Math.random());

            sb.append(alphaNumericString.charAt(index));
        }

        return sb.toString();
    }
}
