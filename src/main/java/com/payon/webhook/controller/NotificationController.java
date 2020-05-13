package com.payon.webhook.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payon.webhook.exceptions.RecordNotFoundException;
import com.payon.webhook.exceptions.WebhookException;
import com.payon.webhook.model.Inbox;
import com.payon.webhook.model.InboxDto;
import com.payon.webhook.model.WebHookNotification;
import com.payon.webhook.repo.InboxRepository;
import com.payon.webhook.repo.WebhookNotificationRepository;
import com.payon.webhook.service.DecryptService;
import com.payon.webhook.service.InboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class NotificationController {

    @Autowired
    InboxRepository inboxRepository;

    @Autowired
    WebhookNotificationRepository webhookNotificationRepository;

    @Autowired
    InboxService inboxService;


    @PostMapping("/webhook-inbox")
    @ResponseBody
    public ResponseEntity<InboxDto> createInbox(HttpServletRequest request) {
        try {
            String name;
            do {
                name = inboxService.createInbox();
            } while (inboxRepository.findByName(name) != null && !inboxRepository.findByName(name).isEmpty());

            System.out.println("Creating new Inbox name = " + name);
            inboxRepository.save(new Inbox(name));

            StringBuffer inboxUrl = request.getRequestURL();
            if (!request.getRequestURL().toString().endsWith("/"))
                inboxUrl.append("/");

            inboxUrl.append(name);

            InboxDto dto = new InboxDto(name, inboxUrl.toString());
            return new ResponseEntity<InboxDto>(dto, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new WebhookException(e.getMessage());
        }
    }

    @PostMapping(value = "/webhook-inbox/{name}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void createNotification(HttpServletRequest request, @PathVariable String name, @RequestBody String data) {
        if (inboxRepository.findByName(name).isEmpty())
            throw new RecordNotFoundException("Inbox '" + name + "' does no exist");
        ObjectMapper objectMapper = new ObjectMapper();
        WebHookNotification notification = null;
        try {
            notification = objectMapper.readValue(data, WebHookNotification.class);
            Inbox inbox = inboxRepository.findByName(name).get(0);
            notification.setAuthHeader(request.getHeader("X-Authentication-Tag"));
            notification.setIvHeader(request.getHeader("X-Initialization-Vector"));
            inbox.getNotifications().add(notification);
            inboxRepository.save(inbox);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new WebhookException(e.getMessage());
        }
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
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readValue(DecryptService.decrypt(notification, configKey), JsonNode.class);
                decryptedNotifications.add(jsonNode.toString()); //remove pretty-printing
            } catch (Exception e) {
                try {
                    decryptedNotifications.add(getErrorString(notification.getId(), e.getMessage()));
                } catch (Exception ex) {
                    System.err.println(e.getMessage());
                    throw new WebhookException(e.getMessage());
                }
            }
        });
        return new ResponseEntity<List<String>>(decryptedNotifications, HttpStatus.OK);
    }

    @GetMapping("/webhook-inbox/{name}/{id}")
    @ResponseBody
    public ResponseEntity<?> getNotificationById(@PathVariable String name, @PathVariable long id, @RequestParam(required = false) String configKey) {
        if (inboxRepository.findByName(name).isEmpty())
            throw new RecordNotFoundException("Inbox '" + name + "' does no exist");
        WebHookNotification notification = webhookNotificationRepository.findById(id).orElseThrow(()
                -> new RecordNotFoundException("Notification does not found for id: " + id));
        if (configKey == null)
            return new ResponseEntity<WebHookNotification>(notification, HttpStatus.OK);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readValue(DecryptService.decrypt(notification, configKey), JsonNode.class);
            return new ResponseEntity<String>(jsonNode.toString(), HttpStatus.OK);
        } catch (Exception e) {
            throw new WebhookException(e.getMessage());
        }
    }

    private String getErrorString(long id, String err) throws Exception {
        HashMap<String, String> map = new HashMap();
        map.put("Error", "An error occurred during decryption for notification id: " + id + "! " + err);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(map);
    }
}
