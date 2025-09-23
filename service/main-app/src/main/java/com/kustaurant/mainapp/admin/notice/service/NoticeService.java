package com.kustaurant.mainapp.admin.notice.service;

import com.kustaurant.mainapp.admin.notice.domain.Notice;
import com.kustaurant.mainapp.admin.notice.infrastructure.NoticeEntity;
import com.kustaurant.mainapp.admin.notice.infrastructure.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public List<Notice> getAllNotices(){
        List<NoticeEntity> entities = noticeRepository.findAll();

        return entities.stream()
                .map(Notice::from)
                .collect(Collectors.toList());
    }
}
