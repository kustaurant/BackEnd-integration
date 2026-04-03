package com.kustaurant.crawler.IGpartnership.service.strategy;

import com.kustaurant.crawler.IGpartnership.dto.ParsedCaption;
import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SocialScienceTargetStrategy implements PartnershipCaptionStrategy {

    private static final Pattern PARTNER_PATTERN =
            Pattern.compile("제휴업체\\s*\\[\\s*([^\\]]+?)\\s*\\]");

    private static final Pattern LOCATION_PATTERN =
            Pattern.compile("📍\\s*업체\\s*위치\\s*:\\s*([\\s\\S]*?)(?=🗝️)");

    private static final Pattern BENEFIT_PATTERN =
            Pattern.compile("🗝️\\s*제휴\\s*혜택\\s*:\\s*([\\s\\S]*?)(?=❗️)");

    @Override
    public boolean supports(PartnershipTarget target) {
        return target == PartnershipTarget.SOCIAL_SCIENCE;
    }

    @Override
    public ParsedCaption parse(String caption) {
        if (caption == null) {
            caption = "";
        }

        String restaurantName = clean(findGroup(PARTNER_PATTERN, caption));
        String benefit = clean(findGroup(BENEFIT_PATTERN, caption));
        String location = clean(findGroup(LOCATION_PATTERN, caption));

        return new ParsedCaption(restaurantName, benefit, location, "");
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