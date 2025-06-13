package com.kustaurant.kustaurant.notice.service;

import com.kustaurant.kustaurant.notice.domain.NoticeDTO;
import com.kustaurant.kustaurant.notice.infrastructure.NoticeEntity;
import com.kustaurant.kustaurant.notice.infrastructure.NoticeRepository;
import com.kustaurant.kustaurant.notice.service.NoticeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class NoticeServiceTest {

    private NoticeRepository noticeRepository;
    private NoticeService noticeService;

    @BeforeEach
    void setUp() {
        noticeRepository = mock(NoticeRepository.class);
        noticeService = new NoticeService(noticeRepository);
    }

    @Test
    void getAllNotices는_noticeDTO리스트들을_잘_불러온다(){
        //g
        NoticeEntity n1= new NoticeEntity();
        n1.setTitle("첫 번째 테스트 공지");
        n1.setHref("https://kustaurant.com");
        n1.setCreatedAt(LocalDate.of(2025,4,2));
        NoticeEntity n2= new NoticeEntity();
        n2.setTitle("두 번째 테스트 공지");
        n2.setHref("https://kustaurant.com");
        n2.setCreatedAt(LocalDate.of(2025,4,3));

        when(noticeRepository.findAll()).thenReturn(Arrays.asList(n1,n2));

        //w
        List<NoticeDTO> result= noticeService.getAllNotices();

        //t
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("첫 번째 테스트 공지");

        //v
        verify(noticeRepository, times(1)).findAll();
    }

}