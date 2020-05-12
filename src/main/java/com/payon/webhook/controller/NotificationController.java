package com.payon.webhook.controller;

import com.google.gson.Gson;
import com.payon.webhook.exceptions.RecordNotFoundException;
import com.payon.webhook.model.Inbox;
import com.payon.webhook.model.WebHookNotification;
import com.payon.webhook.repo.InboxRepository;
import com.payon.webhook.service.DecryptService;
import com.payon.webhook.service.InboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class NotificationController {

    @Autowired
    InboxRepository inboxRepository;

    @Autowired
    InboxService inboxService;


    @PostMapping("/webhook-inbox")
    public String createInbox(HttpServletRequest request) {
        String name;
        do {
            name = inboxService.createInbox();
        } while (inboxRepository.findByName(name) != null && !inboxRepository.findByName(name).isEmpty());

        System.out.println("Creating new Inbox name = " + name);
        inboxRepository.save(new Inbox(name));
        String inboxUrl = request.getRequestURL().append("/").append(name).toString();
        System.out.println("Inbox url===>" + inboxUrl);
        return inboxUrl;
    }

    @PostMapping(value = "/webhook-inbox/{name}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void createNotification(HttpServletRequest request, @PathVariable String name, @RequestBody String data) {
        if (inboxRepository.findByName(name).isEmpty())
            throw new RecordNotFoundException("Inbox '" + name + "' does no exist");
        WebHookNotification notification = new Gson().fromJson(data, WebHookNotification.class);
        Inbox inbox = inboxRepository.findByName(name).get(0);
        notification.setAuthHeader(request.getHeader("X-Authentication-Tag"));
        notification.setIvHeader(request.getHeader("X-Initialization-Vector"));
        inbox.getNotifications().add(notification);
        inboxRepository.save(inbox);
    }

    @GetMapping("/webhook-inbox/{name}")
    @ResponseBody
    public ResponseEntity<?> getNotifications(@PathVariable String name, @RequestParam(required = false) String configKey) {
        if (inboxRepository.findByName(name).isEmpty())
            throw new RecordNotFoundException("Inbox '" + name + "' does no exist");
        if (configKey == null)
            return new ResponseEntity<List<WebHookNotification>>(inboxRepository.findByName(name).get(0).getNotifications(), HttpStatus.OK);

        List<String> decryptedNotifications = new ArrayList<>();

        inboxRepository.findByName(name).get(0).getNotifications().forEach(notification -> {
            try {
                decryptedNotifications.add(DecryptService.decrypt(notification, configKey));
            } catch (Exception e) {
                decryptedNotifications.add(getErrorString(notification.getId(), e.getMessage()));
            }
        });
        System.out.println(decryptedNotifications);
        return new ResponseEntity<List<String>>(decryptedNotifications, HttpStatus.OK);
    }

    private String getErrorString(long id, String err) {
        HashMap<String, String> map = new HashMap();
        map.put("Error", "An error occurred during decryption for notification id: " + id + "! " + err);
        return new Gson().toJson(map);
    }
}
