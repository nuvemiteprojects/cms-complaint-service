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
public class CompanyCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(CompanyCreatedConsumer.class);

    private final ObjectMapper objectMapper;
    private final InboxService inboxService;
    private final ReferenceDataService referenceDataService;

    public CompanyCreatedConsumer(
            ObjectMapper objectMapper, InboxService inboxService, ReferenceDataService referenceDataService) {
        this.objectMapper = objectMapper;
        this.inboxService = inboxService;
        this.referenceDataService = referenceDataService;
    }

    @KafkaListener(topics = "${cms.kafka.topics.company-created}")
    @Transactional
    public void onCompanyCreated(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String eventId = textOrNull(root, "eventId");
            if (eventId == null) {
                eventId = root.path("id").asText(null);
            }
            if (eventId == null || eventId.isBlank()) {
                log.warn("Skipping company.created message without eventId");
                return;
            }
            if (!inboxService.tryMarkProcessed(eventId)) {
                return;
            }
            String companyIdText = textOrNull(root, "companyId");
            if (companyIdText == null || companyIdText.isBlank()) {
                log.warn("Skipping company.created {} without companyId", eventId);
                return;
            }
            UUID companyId = UUID.fromString(companyIdText);
            String legalName = textOrNull(root, "legalName");
            referenceDataService.registerCompany(companyId, legalName);
            log.info("Registered company reference {} from event {}", companyId, eventId);
        } catch (Exception ex) {
            log.error("Failed to process company.created message", ex);
            throw new IllegalStateException("Failed to process company.created event", ex);
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
