package com.payon.webhook.repo;

import com.payon.webhook.domain.Inbox;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "inbox", path = "inbox")
public interface InboxRepository extends PagingAndSortingRepository<Inbox, Long> {
    List<Inbox> findByName(@Param("name") String name);

    @Override
    @RestResource(exported = false)
    void delete(Inbox entity);

    // We don't expose this method via rest here as we want to extend the logic.
    // It is exposed in controller.
    @Override
    @RestResource(exported = false)
    Inbox save(Inbox entity);
}
