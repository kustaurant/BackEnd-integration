package com.kustaurant.kustaurant.admin.RestaurantSync.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
// raw 카테고리를 고정 음식종류로 매핑하고 수동 지정값을 검증하는 서비스.
public class CuisineMappingService {

    private static final List<String> FIXED_CUISINES = List.of(
            "한식", "일식", "중식", "양식", "아시안", "고기", "치킨",
            "해산물", "패스트푸드/분식", "분식", "주점", "카페/디저트", "베이커리", "샐러드"
    );

    public String resolveCuisineForNew(String rawCategory, String manualCuisine) {
        String normalizedManual = normalizeToNull(manualCuisine);
        if (normalizedManual != null && !FIXED_CUISINES.contains(normalizedManual)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid manualCuisine: " + manualCuisine);
        }

        String autoMapped = mapRawCategoryToFixedCuisine(rawCategory);
        if (autoMapped != null) {
            return autoMapped;
        }
        if (normalizedManual != null) {
            return normalizedManual;
        }
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "자동 매핑 실패: manualCuisine(고정 음식종류) 값을 지정해 주세요."
        );
    }

    public String mapRawCategoryToFixedCuisine(String rawCategory) {
        String normalized = normalizeToNull(rawCategory);
        if (normalized == null) return null;
        if (FIXED_CUISINES.contains(normalized)) return normalized;

        if (containsAny(normalized, "찜닭", "백반", "한정식", "국밥", "냉면", "순대국", "된장국")) return "한식";
        if (containsAny(normalized, "스시", "초밥", "우동", "라멘", "돈카츠", "이자카야", "덮밥", "돈까스")) return "일식";
        if (containsAny(normalized, "중식", "중국집", "짜장", "짬뽕", "탕수육", "마라", "딤섬")) return "중식";
        if (containsAny(normalized, "파스타", "스테이크", "리조또", "브런치", "이탈리안", "프렌치")) return "양식";
        if (containsAny(normalized, "아시안", "쌀국수", "태국", "베트남", "인도", "동남아")) return "아시안";
        if (containsAny(normalized, "고깃집", "삼겹", "갈비", "곱창", "대창", "막창", "육회", "족발", "보쌈", "양꼬치", "조개구이", "소고기")) return "고기";
        if (containsAny(normalized, "치킨", "통닭", "닭발", "프라이드", "양념치킨", "후라이드")) return "치킨";
        if (containsAny(normalized, "해산물", "횟집", "생선", "대게", "조개", "낙지", "파스타", "주꾸미")) return "해산물";
        if (containsAny(normalized, "패스트푸드", "버거", "피자", "핫도그")) return "패스트푸드/분식";
        if (containsAny(normalized, "분식", "떡볶이", "김밥", "쫄면", "순대", "튀김")) return "분식";
        if (containsAny(normalized, "주점", "술집", "사케", "호프", "바", "와인바", "이자카야", "포차")) return "주점";
        if (containsAny(normalized, "카페", "커피", "디저트", "빙수", "도넛", "젤라또", "마카롱")) return "카페/디저트";
        if (containsAny(normalized, "베이커리", "빵집", "제과", "제빵", "브런치")) return "베이커리";
        if (containsAny(normalized, "샐러드", "샤브", "그레인", "비건")) return "샐러드";
        return null;
    }

    private boolean containsAny(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) return true;
        }
        return false;
    }

    private String normalizeToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
