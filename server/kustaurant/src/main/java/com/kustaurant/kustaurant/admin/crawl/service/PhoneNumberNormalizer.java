package com.kustaurant.kustaurant.admin.crawl.service;

import org.springframework.stereotype.Component;

@Component
public class PhoneNumberNormalizer {
    public String normalize(String raw) {
        if (raw == null || raw.isEmpty()) return null;

        String digits = raw.replaceAll("\\D", "");
        if (digits.startsWith("82") && digits.length() > 2) {
            digits = "0" + digits.substring(2);
        }
        return digits;
    }
}
