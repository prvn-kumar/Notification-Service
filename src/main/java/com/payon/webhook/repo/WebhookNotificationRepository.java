package com.payon.webhook.repo;

import com.payon.webhook.domain.WebHookNotification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource(exported = false)
@Transactional(readOnly = true)
public interface WebhookNotificationRepository extends CrudRepository<WebHookNotification, Long> {

}
