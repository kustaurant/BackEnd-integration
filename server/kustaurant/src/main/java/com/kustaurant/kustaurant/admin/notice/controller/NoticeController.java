package com.kustaurant.kustaurant.admin.notice.controller;

import com.kustaurant.kustaurant.admin.notice.domain.Notice;
import com.kustaurant.kustaurant.admin.notice.service.NoticeService;
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
        List<Notice> notices = noticeService.getAllNotices();
        model.addAttribute("notices", notices);
        return "home/notice";
    }

}
