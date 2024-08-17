package com.kustaurant.restauranttier.tab1_home.controller;

import com.kustaurant.restauranttier.tab1_home.entity.Notice;
import com.kustaurant.restauranttier.tab1_home.repository.NoticeRepository;
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
        List<Notice> notice= noticeRepo.findAll();
        model.addAttribute("Notice",notice);
        return "notice";
    }

}
