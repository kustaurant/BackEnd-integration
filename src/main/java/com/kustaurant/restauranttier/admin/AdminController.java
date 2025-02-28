package com.kustaurant.restauranttier.admin;

import com.kustaurant.restauranttier.tab3_tier.etc.CuisineEnum;
import com.kustaurant.restauranttier.tab3_tier.etc.LocationEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    // 관리자 화면 - 메인 화면 로드
    @GetMapping
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
    public String admin(Model model) {
        model.addAttribute("title", "메인");
        model.addAttribute("content", "main");
        return "admin/admin";
    }

    // 관리자 화면 - 식당 수정 화면 로드
    @GetMapping("/restaurants/{id}/revise")
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
    public String reviseRestaurant(
            @PathVariable int id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            model.addAllAttributes(Map.of(
                    "title", "식당 정보 수정",
                    "content", "revise-restaurant",
                    "locations", Arrays.stream(LocationEnum.values())
                            .map(LocationEnum::getValue)
                            .filter(l -> !l.equals(LocationEnum.ALL.getValue()))
                            .toList(),
                    "cuisines", Arrays.stream(CuisineEnum.values())
                            .map(CuisineEnum::getValue)
                            .filter(c -> !c.equals(CuisineEnum.ALL.getValue()) && !c.equals(CuisineEnum.JH.getValue()))
                            .toList(),
                    "restaurantInfo", adminService.getRestaurantInfo(id).get(),
                    "id", id
            ));
        } catch (Exception e) {
            log.error("[AdminController][reviseRestaurant] {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "식당 데이터 조회 또는 변환 과정에서 오류가 발생했습니다.");
            return "redirect:/admin";
        }
        return "admin/admin";
    }

    // 식당 정보 수정 요청
    @PostMapping("/restaurants/{id}/revise")
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
    public String reviseRestaurant(
            @PathVariable int id,
            RedirectAttributes redirectAttributes,
            @ModelAttribute RestaurantInfoDto restaurantInfoDto
    ) {
        // 데이터 갱신
        try {
            adminService.updateRestaurantInfo(id, restaurantInfoDto);
            redirectAttributes.addFlashAttribute("successMessage", "식당 데이터 수정이 성공적으로 완료되었습니다.");
        } catch (Exception e) { // 트랜잭션 과정에서 예외 발생 시 오류 페이지
            log.error("[AdminController][reviseRestaurant] {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "식당 정보 갱신 과정에서 오류가 발생했습니다.");
        }
        return "redirect:/admin";
    }

    // 관리자 화면 - 식당 추가 화면 로드
    @GetMapping("/restaurants/add")
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
    public String addRestaurant(Model model) {
        // model에 데이터 채우기
        model.addAllAttributes(Map.of(
                "title", "식당 추가",
                "content", "add-restaurant",
                "locations", Arrays.stream(LocationEnum.values())
                        .map(LocationEnum::getValue)
                        .filter(l -> !l.equals(LocationEnum.ALL.getValue()))
                        .toList(),
                "cuisines", Arrays.stream(CuisineEnum.values())
                        .map(CuisineEnum::getValue)
                        .filter(c -> !c.equals(CuisineEnum.ALL.getValue()) && !c.equals(CuisineEnum.JH.getValue()))
                        .toList()
        ));
        return "admin/admin";
    }

    // 식당 정보 추가 요청
    @PostMapping("/restaurants/add")
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
    public String addRestaurant(
            @RequestParam MultiValueMap<String, String> dataMap,
            RedirectAttributes redirectAttributes
    ) {
        try {
            adminService.addRestaurantInfos(dataMap);
            redirectAttributes.addFlashAttribute("successMessage", "식당 데이터 추가가 성공적으로 완료되었습니다.");
        } catch (Exception e) { // 트랜잭션 과정에서 예외 발생 시 오류 페이지
            log.error("[AdminController][addRestaurant] {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "식당 데이터 저장 과정에서 오류가 발생했습니다.");
        }
        return "redirect:/admin";
    }
}
