package com.kustaurant.kustaurant.api.restaurant;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
@Service
@RequiredArgsConstructor
public class DrawApiSerivce {

    public List<RestaurantEntity> getRandomSubList(List<RestaurantEntity> originalList, int targetSize) {
        List<RestaurantEntity> resultList = new ArrayList<>();
        Random rand = new Random();

        // 원본 리스트가 targetSize보다 작으면, 반복해서 추가
        while (resultList.size() < targetSize) {
            resultList.addAll(originalList);
        }
        // 결과 리스트를 랜덤으로 섞음
        Collections.shuffle(resultList, rand);
        // 결과 리스트에서 targetSize만큼 잘라서 반환
        return new ArrayList<>(resultList.subList(0, targetSize));
    }
}
