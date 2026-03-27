package com.kustaurant.jpa.restaurant.enums;

public enum PartnershipTarget {
    ALL("재학생모두"),
    ENGINEERING("공과대학"),
    EDUCATION("사범대학"),
    SOCIAL_SCIENCE("사회과학대학"),
    LIFE_SCIENCE("생명과학대학"),
    VETERINARY("수의과대학"),
    ART_DESIGN("예술디자인대학"),
    CONVERGENCE_SCI_TECH("융합과학기술원"),
    SCIENCE("이과대학"),
    CLUBUNION("동아리연합"),
    WELFARE("학생복지위원회");


    private final String koreanName;

    PartnershipTarget(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
