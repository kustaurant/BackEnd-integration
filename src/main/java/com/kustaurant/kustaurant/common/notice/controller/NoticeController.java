package com.kustaurant.kustaurant.common.notice.controller;

import com.kustaurant.kustaurant.common.notice.infrastructure.NoticeEntity;
import com.kustaurant.kustaurant.common.notice.infrastructure.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class NoticeController {
    private final NoticeRepository noticeRepo;
    @GetMapping("/notice")
    public String notice(
            Model model
    ){
        List<NoticeEntity> noticeEntity = noticeRepo.findAll();
        model.addAttribute("Notice", noticeEntity);
        return "notice";
    }

}
