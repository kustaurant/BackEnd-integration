package com.kustaurant.kustaurant.common.notice.controller;

import com.kustaurant.kustaurant.common.notice.domain.NoticeDTO;
import com.kustaurant.kustaurant.common.notice.infrastructure.NoticeEntity;
import com.kustaurant.kustaurant.common.notice.infrastructure.NoticeRepository;
import com.kustaurant.kustaurant.common.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/notice")
    public String getNotices(
            Model model
    ){
        List<NoticeDTO> notices = noticeService.getAllNotices();
        model.addAttribute("notices", notices);
        return "notice";
    }

}
