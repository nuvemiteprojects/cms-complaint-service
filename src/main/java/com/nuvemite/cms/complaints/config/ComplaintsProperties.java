package com.nuvemite.cms.complaints.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cms.complaints")
public record ComplaintsProperties(Outbox outbox) {

    public record Outbox(long pollIntervalMs, int batchSize) {}
}
