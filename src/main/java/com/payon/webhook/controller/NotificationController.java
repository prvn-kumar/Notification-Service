package com.payon.webhook.controller;

import com.google.gson.GsonBuilder;
import com.payon.webhook.domain.Inbox;
import com.payon.webhook.domain.WebHookNotification;
import com.payon.webhook.exceptions.RecordNotFoundException;
import com.payon.webhook.exceptions.WebhookException;
import com.payon.webhook.model.DecryptedNotificationDto;
import com.payon.webhook.model.InboxDto;
import com.payon.webhook.model.Notification;
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
import java.util.ArrayList;
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
    public void createNotification(HttpServletRequest request, @PathVariable String name, @RequestBody WebHookNotification notification) {
        if (inboxRepository.findByName(name).isEmpty())
            throw new RecordNotFoundException("Inbox '" + name + "' does no exist");
        Inbox inbox = inboxRepository.findByName(name).get(0);

        try {
            notification.setAuthHeader(request.getHeader("X-Authentication-Tag"));
            notification.setIvHeader(request.getHeader("X-Initialization-Vector"));
            inbox.getNotifications().add(notification);
            inboxRepository.save(inbox);
        } catch (Exception e) {
            throw new WebhookException(e.getMessage());
        }
    }

    @PostMapping(value = "/webhook-inbox/{name}", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void createNotificationTextPlain(HttpServletRequest request, @PathVariable String name, @RequestBody String encryptedText) {
        if (inboxRepository.findByName(name).isEmpty())
            throw new RecordNotFoundException("Inbox '" + name + "' does no exist");
        Inbox inbox = inboxRepository.findByName(name).get(0);

        try {
            WebHookNotification notification = new WebHookNotification();
            notification.setEncryptedBody(encryptedText);
            notification.setAuthHeader(request.getHeader("X-Authentication-Tag"));
            notification.setIvHeader(request.getHeader("X-Initialization-Vector"));
            inbox.getNotifications().add(notification);
            inboxRepository.save(inbox);
        } catch (Exception e) {
            throw new WebhookException(e.getMessage());
        }
    }

    @GetMapping("/webhook-inbox/{name}")
    public InboxDto getInbox(@PathVariable String name, HttpServletRequest request) {
        List<Inbox> inboxResult = inboxRepository.findByName(name);
        if (inboxResult.isEmpty())
            throw new RecordNotFoundException("Inbox '" + name + "' does no exist");

        InboxDto dto = new InboxDto(name, request.getRequestURL().toString());
        dto.setConfigKeySaved(inboxResult.get(0).getConfigurationKey() != null
                && !inboxResult.get(0).getConfigurationKey().isEmpty());
        dto.setNotificationCount(inboxResult.get(0).getNotifications().size());
        return dto;
    }

    @PostMapping(value = "/webhook-inbox/{name}/config/{key}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveConfigKey(@PathVariable String name, @PathVariable String key) {
        checkAndSaveConfig(name, key, true);
    }

    @DeleteMapping(value = "/webhook-inbox/{name}/config/{key}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void removeConfigKey(@PathVariable String name, @PathVariable String key) {
        if (inboxRepository.findByName(name).isEmpty())
            throw new RecordNotFoundException("Inbox '" + name + "' does no exist");

        Inbox inbox = inboxRepository.findByName(name).get(0);

        if (key != null && !key.isEmpty() && key.equals(inbox.getConfigurationKey())) {
            inbox.setConfigurationKey(null);
            inboxRepository.save(inbox);
        } else {
            throw new RecordNotFoundException("Configuration key '" + key + "' does no exist!");
        }
    }

    @GetMapping("/webhook-inbox/{name}/notification")
    public List<? extends WebHookNotification> getNotifications(@PathVariable String name,
                                                                @RequestParam(required = false) String configKey,
                                                                @RequestParam(required = false) boolean saveConfigKey) {

        Inbox inbox = checkAndSaveConfig(name, configKey, saveConfigKey);

        String decryptionKey = getDecryptionKey(inbox, configKey);

        if (decryptionKey == null || decryptionKey.isEmpty())
            return inboxRepository.findByName(name).get(0).getNotifications();

        List<DecryptedNotificationDto> decryptedNotifications = new ArrayList<>();

        inboxRepository.findByName(name).get(0).getNotifications().forEach(notification -> {
            decryptedNotifications.add(getDecryptedNotification(notification, decryptionKey));
        });
        return decryptedNotifications;
    }

    @GetMapping("/webhook-inbox/{name}/notification/{id}")
    public WebHookNotification getNotificationById(@PathVariable String name, @PathVariable long id,
                                                   @RequestParam(required = false) String configKey,
                                                   @RequestParam(required = false) boolean saveConfigKey) {

        Inbox inbox = checkAndSaveConfig(name, configKey, saveConfigKey);

        WebHookNotification notification = webhookNotificationRepository.findById(id).orElseThrow(()
                -> new RecordNotFoundException("Notification does not found for id: " + id));

        String decryptionKey = getDecryptionKey(inbox, configKey);
        if (decryptionKey == null || decryptionKey.isEmpty())
            return notification;

        return getDecryptedNotification(notification, decryptionKey);
    }

    private Inbox checkAndSaveConfig(String name, String configKey, boolean saveConfigKey) {
        if (inboxRepository.findByName(name).isEmpty())
            throw new RecordNotFoundException("Inbox '" + name + "' does no exist");

        Inbox inbox = inboxRepository.findByName(name).get(0);

        if (saveConfigKey && configKey != null && !configKey.isEmpty()) {
            inbox.setConfigurationKey(configKey);
            inboxRepository.save(inbox);
        }
        return inbox;
    }

    private DecryptedNotificationDto getDecryptedNotification(WebHookNotification notification, String decryptionKey) {
        try {
            String json = DecryptService.decrypt(notification, decryptionKey);
            Notification content = new GsonBuilder().disableHtmlEscaping().create().fromJson(json, Notification.class);
            DecryptedNotificationDto decrypted = new DecryptedNotificationDto();
            decrypted.setDecryptedContent(content);
            decrypted.setId(notification.getId());
            decrypted.setCreatedTime(notification.getCreatedTime());
            return decrypted;
        } catch (Exception e) {
            throw new WebhookException("Error while decrypting notifications! " + e.getMessage());
        }
    }

    private String getDecryptionKey(Inbox inbox, String configKey) {
        return configKey != null && !configKey.isEmpty() ? configKey : inbox.getConfigurationKey();
    }
}
