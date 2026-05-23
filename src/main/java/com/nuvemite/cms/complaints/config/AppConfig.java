package com.nuvemite.cms.complaints.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ComplaintsProperties.class, KafkaTopicsProperties.class})
public class AppConfig {}
