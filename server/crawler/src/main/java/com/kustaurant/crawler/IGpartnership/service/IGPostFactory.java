package com.kustaurant.crawler.IGpartnership.service;

import com.kustaurant.crawler.IGpartnership.dto.ParsedCaption;
import com.kustaurant.crawler.IGpartnership.dto.RawPost;
import com.kustaurant.jpa.restaurant.IGPost;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IGPostFactory {
    private static final String INSTAGRAM_BASE_URL = "https://www.instagram.com";

    public Optional<IGPost> create(RawPost rawPost, ParsedCaption parsed) {
        if (rawPost == null || parsed == null) {
            return Optional.empty();
        }

        if (rawPost.code() == null || rawPost.code().isBlank()) {
            return Optional.empty();
        }

        String postUrl = INSTAGRAM_BASE_URL + "/p/" + rawPost.code() + "/";

        return Optional.of(new IGPost(
                postUrl,
                parsed.restaurantName(),
                parsed.benefit(),
                parsed.location(),
                parsed.contact()
        ));
    }
}
