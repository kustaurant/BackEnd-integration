package com.kustaurant.kustaurant.admin.crawl.service.matching;

import org.springframework.stereotype.Component;

@Component
public class PhoneNumberNormalizer {
    public String normalize(String raw) {
        if (raw == null || raw.isEmpty()) return null;

        String digits = raw.replaceAll("\\D", "");
        if (digits.isBlank()) return null;

        if (digits.startsWith("82") && digits.length() > 2) digits = "0" + digits.substring(2);

        return digits;
    }
}
