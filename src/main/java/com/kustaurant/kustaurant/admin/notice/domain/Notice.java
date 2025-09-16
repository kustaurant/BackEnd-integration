package com.kustaurant.kustaurant.admin.notice.domain;

import com.kustaurant.kustaurant.admin.notice.infrastructure.NoticeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Notice {
    private String title;
    private String href;
    private String createdAt;

    public static Notice from(NoticeEntity noticeEntity) {
        return new Notice(
                noticeEntity.getTitle(),
                noticeEntity.getHref(),
                noticeEntity.getCreatedAt().toString()
        );
    }
}
