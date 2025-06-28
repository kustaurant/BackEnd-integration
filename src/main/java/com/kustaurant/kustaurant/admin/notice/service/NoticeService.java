package com.kustaurant.kustaurant.admin.notice.service;

import com.kustaurant.kustaurant.admin.notice.domain.NoticeDTO;
import com.kustaurant.kustaurant.admin.notice.infrastructure.NoticeEntity;
import com.kustaurant.kustaurant.admin.notice.infrastructure.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public List<NoticeDTO> getAllNotices(){
        List<NoticeEntity> entities = noticeRepository.findAll();

        return entities.stream()
                .map(NoticeDTO::from)
                .collect(Collectors.toList());
    }
}
