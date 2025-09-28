package com.kustaurant.kustaurant.admin.notice;

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
