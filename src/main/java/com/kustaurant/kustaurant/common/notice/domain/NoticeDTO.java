package com.kustaurant.kustaurant.common.notice.domain;

import com.kustaurant.kustaurant.common.notice.infrastructure.NoticeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NoticeDTO {
    private String title;
    private String href;
    private String createdAt;

    public static NoticeDTO from(NoticeEntity noticeEntity) {
        return new NoticeDTO(
                noticeEntity.getTitle(),
                noticeEntity.getHref(),
                noticeEntity.getCreatedAt().toString()
        );
    }
}
