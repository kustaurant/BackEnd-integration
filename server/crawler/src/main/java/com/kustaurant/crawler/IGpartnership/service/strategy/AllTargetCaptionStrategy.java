package com.kustaurant.crawler.IGpartnership.service.strategy;

import com.kustaurant.crawler.IGpartnership.dto.ParsedCaption;
import com.kustaurant.restaurant.enums.PartnershipTarget;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AllTargetCaptionStrategy implements PartnershipCaptionStrategy {
    // ㅇㅇㅇ의 제휴업체 [ㅇㅇㅇ]을 소개합니다.
    private static final Pattern PARTNER_BRACKET =
            Pattern.compile("제휴업체\\s*\\[\\s*([^\\]\\n]+?)\\s*\\]");

    // 🤝제휴 혜택 : 총액의 10% 할인
    // 다음 이모지/구역(▶️, 📍, 감사, 더 많은 혜택...) 나오기 전까지 추출
    private static final Pattern BENEFIT_PATTERN =
            Pattern.compile("🤝\\s*제휴\\s*혜택\\s*:\\s*([\\s\\S]*?)(?=▶️|📍|감사합니다|더\\s*많은\\s*혜택|$)");

    // 📍업체 위치 : ㅇㅇㅇ
    // 다음 이모지/구역(🤝, ▶️, 감사, 더 많은 혜택...) 나오기 전까지 추출
    private static final Pattern LOCATION_PATTERN =
            Pattern.compile("📍\\s*업체\\s*위치\\s*:\\s*([\\s\\S]*?)(?=🤝|▶️|감사합니다|더\\s*많은\\s*혜택|$)");

    @Override
    public boolean supports(PartnershipTarget target) {
        return target == PartnershipTarget.ALL;
    }

    @Override
    public ParsedCaption parse(String caption) {
        if (caption == null) {
            caption = "";
        }

        String restuarantName = clean(findGroup(PARTNER_BRACKET, caption));
        String benefit = clean(findGroup(BENEFIT_PATTERN, caption));
        String location = clean(findGroup(LOCATION_PATTERN, caption));

        return new ParsedCaption(restuarantName, benefit, location, "");
    }

    @Override
    public boolean hasRequiredFields(ParsedCaption parsedCaption) {
        if (parsedCaption == null) return false;

        return hasText(parsedCaption.restaurantName()) && hasText(parsedCaption.location()) && hasText(parsedCaption.benefit());
    }

    private String findGroup(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) return matcher.group(1).trim();

        return "";
    }

    private String clean(String value) {
        if (value == null) return "";

        return value.replaceAll("\\s+", " ").trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
