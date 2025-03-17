package com.kustaurant.kustaurant.common.notice;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NoticeDTO {
    private String noticeTitle;
    private String noticeLink;
    private String createdDate;
}
