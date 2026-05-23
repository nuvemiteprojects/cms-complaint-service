package com.nuvemite.cms.complaints.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cms.kafka.topics")
public record KafkaTopicsProperties(
        String companyCreated,
        String premiseCreated,
        String complaintInspectionRequested,
        String complaintSubmitted,
        String complaintResolved) {}
