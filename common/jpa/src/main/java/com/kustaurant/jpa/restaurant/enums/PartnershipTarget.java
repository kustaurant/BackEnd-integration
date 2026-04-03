package com.kustaurant.jpa.restaurant.enums;

public enum PartnershipTarget {
    ALL("재학생모두"), //
    ENGINEERING("공과대학"), //식당없음
    LIBERAL("문과대학"), //식당없음
    EDUCATION("사범대학"), //식당없음
    SOCIAL_SCIENCE("사회과학대학"), //
    LIFE_SCIENCE("생명과학대학"), //
    VETERINARY("수의과대학"), //
    ART_DESIGN("예술디자인대학"),
    CONVERGENCE_SCI_TECH("융합과학기술원"), //식당없음
    SCIENCE("이과대학"), //식당없음
    CLUBUNION("동아리연합"), //식당없음
    WELFARE("학생복지위원회"); //식당없음


    private final String koreanName;

    PartnershipTarget(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
