package com.kustaurant.dataprocessor.aianalysis.domain.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RestaurantAnalysis {

    private int reviewCount;
    private int positiveCount;
    private int negativeCount;
    private int scoreSum;
    private double avgScore;
    private Map<String, Integer> situationCounts;

    public static RestaurantAnalysis of(List<ReviewAnalysis> analyses) {
        int total = analyses.size();
        int pos = 0, neg = 0, sum = 0;
        Map<String, Integer> map = new HashMap<>();
        for (ReviewAnalysis r : analyses) {
            if (r.sentiment() == Sentiment.POSITIVE) {
                pos++;
            } else if (r.sentiment() == Sentiment.NEGATIVE) {
                neg++;
            }
            sum += r.score();
            for (String s : r.situations()) {
                map.merge(s, 1, Integer::sum);
            }
        }
        return new RestaurantAnalysis(total, pos, neg, sum, sum * 1d / total, map);
    }
}
