package com.nuvemite.cms.complaints.service;

import java.time.Year;
import org.springframework.stereotype.Component;

@Component
public class ComplaintReferenceNumberGenerator {

    public String next(long sequence) {
        return "CMP-" + Year.now().getValue() + "-" + String.format("%05d", sequence + 1);
    }
}
