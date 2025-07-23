package com.kustaurant.kustaurant.home;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeBannerApiService {
    public List<String> getHomeBannerImage() {
        List<String> homePhotoUrls = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            StringBuilder imagePathBuilder = new StringBuilder();
            imagePathBuilder.append("/배너").append(i).append(".png");

            StringBuilder imageUrlBuilder = new StringBuilder();
            imageUrlBuilder.append("https://kustaurant.s3.ap-northeast-2.amazonaws.com/home").append(imagePathBuilder);

            homePhotoUrls.add(imageUrlBuilder.toString());
        }
        return homePhotoUrls;
    }
}
