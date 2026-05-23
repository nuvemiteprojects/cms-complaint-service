package com.nuvemite.cms.complaints.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuvemite.cms.complaints.service.InboxService;
import com.nuvemite.cms.complaints.service.ReferenceDataService;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PremiseCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(PremiseCreatedConsumer.class);

    private final ObjectMapper objectMapper;
    private final InboxService inboxService;
    private final ReferenceDataService referenceDataService;

    public PremiseCreatedConsumer(
            ObjectMapper objectMapper, InboxService inboxService, ReferenceDataService referenceDataService) {
        this.objectMapper = objectMapper;
        this.inboxService = inboxService;
        this.referenceDataService = referenceDataService;
    }

    @KafkaListener(topics = "${cms.kafka.topics.premise-created}")
    @Transactional
    public void onPremiseCreated(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String eventId = textOrNull(root, "eventId");
            if (eventId == null) {
                eventId = root.path("id").asText(null);
            }
            if (eventId == null || eventId.isBlank()) {
                log.warn("Skipping premise.created message without eventId");
                return;
            }
            if (!inboxService.tryMarkProcessed(eventId)) {
                return;
            }
            String premiseIdText = textOrNull(root, "premiseId");
            String companyIdText = textOrNull(root, "companyId");
            if (premiseIdText == null || companyIdText == null) {
                log.warn("Skipping premise.created {} without premiseId/companyId", eventId);
                return;
            }
            UUID premiseId = UUID.fromString(premiseIdText);
            UUID companyId = UUID.fromString(companyIdText);
            String name = textOrNull(root, "name");
            referenceDataService.registerPremise(premiseId, companyId, name);
            referenceDataService.registerCompany(companyId, null);
            log.info("Registered premise reference {} from event {}", premiseId, eventId);
        } catch (Exception ex) {
            log.error("Failed to process premise.created message", ex);
            throw new IllegalStateException("Failed to process premise.created event", ex);
        }
    }

    private static String textOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        return value.asText();
    }
}
