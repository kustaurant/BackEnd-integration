package com.kustaurant.crawler.IGpartnership.service.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CaptionParser {

    // 인스타에서 나오는 각종 따옴표들
    private static final String QUOTES = "‘’\"“”'";

    // 제휴업체 'OOO' / 제휴업체 ‘OOO’ / 제휴업체 “OOO”
    private static final Pattern PARTNER_QUOTED =
            Pattern.compile("제휴업체\\s*[" + QUOTES + "]\\s*([^\\n" + QUOTES + "]+?)\\s*[" + QUOTES + "]");

    // 혜택: "🔥 제휴 혜택 :" ~ "📍" 전까지
    private static final Pattern BENEFIT_PATTERN =
            Pattern.compile("🔥\\s*제휴\\s*혜택\\s*:\\s*([\\s\\S]*?)(?=📍)");

    // 위치: "📍 위치 :" ~ "📞" 전까지
    private static final Pattern LOCATION_PATTERN =
            Pattern.compile("📍\\s*위치\\s*:\\s*([\\s\\S]*?)(?=📞)");

    // 연락처: "📞 연락처 :" ~ "❗" 또는 끝까지
    private static final Pattern CONTACT_PATTERN =
            Pattern.compile("📞\\s*연락처\\s*:\\s*([\\s\\S]*?)(?=❗|$)");

    public static Parsed parse(String caption) {
        if (caption == null) caption = "";
        String partnerRaw = findGroup(PARTNER_QUOTED, caption);
        String partner = cleanPartner(partnerRaw);

        String benefit = clean(findGroup(BENEFIT_PATTERN, caption));
        String location = clean(findGroup(LOCATION_PATTERN, caption));
        String contact = clean(findGroup(CONTACT_PATTERN, caption));

        return new Parsed(partner, benefit, location, contact);
    }

    private static String findGroup(Pattern p, String text) {
        Matcher m = p.matcher(text);
        if (m.find()) return m.group(1).trim();
        return "";
    }

    private static String clean(String s) {
        return s.replaceAll("\\s+", " ").trim();
    }

    private static String cleanPartner(String s) {
        if (s == null) return "";
        // 따옴표, 소개합니다 꼬리 정리
        s = s.replaceAll("[" + QUOTES + "]", "");
        s = s.replaceAll("(을|를)\\s*소개.*$", "");
        s = s.replaceAll("\\s+", " ").trim();
        return s;
    }

    public record Parsed(String partner, String benefit, String location, String contact) {}
}