package com.kustaurant.kustaurant.v1.community.dto;

import lombok.Data;

@Data
public class ScrapToggleDTO {
    Integer scrapCount;
    Integer status;

    public ScrapToggleDTO(Integer scrapCount, Integer status) {
        this.scrapCount = scrapCount;
        this.status = status;
    }


}