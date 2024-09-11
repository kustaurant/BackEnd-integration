package com.kustaurant.restauranttier.crawling;

import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantMenu;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantMenuRepository;
import com.kustaurant.restauranttier.tab3_tier.service.RestaurantApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kustaurant.restauranttier.crawling.SelectorConst.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class MenuCrawlingTask {
    private final RestaurantApiService restaurantApiService;
    private final RestaurantMenuRepository restaurantMenuRepository;

    public void exec(List<Integer> restaurantIds, List<Integer> failedRestaurantIds) {
        Crawling crawling = new Crawling().setWaitTime(10).maximizeWindow();
        for (Integer restaurantId : restaurantIds) {
            try {
                log.info("[{} id restaurant 시작]", restaurantId);
                Restaurant restaurant = restaurantApiService.findRestaurantById(restaurantId);

                crawlingMenus(crawling, restaurant);
            } catch (Exception e) {
                log.info("[{} id restaurant 크롤링 실패]", restaurantId);
                failedRestaurantIds.add(restaurantId);
            }
        }

        crawling.quitDriver();
    }

    private void crawlingMenus(Crawling crawling, Restaurant restaurant) {
        String url = restaurant.getRestaurantUrl();
        if (url == null || url.isEmpty()) {
            log.error("식당의 url 정보가 없습니다. 식당 id: {}", restaurant.getRestaurantId());
            throw new RuntimeException();
        }

        crawling.openUrl(url);
        // iframe 전환
        crawling.changeIframe(entryIframe);

        List<WebElement> menuButtons = crawling.getElementsBySelector(menuBar);
        WebElement menuButton = menuButtons.stream()
                .filter(btn -> btn.getText().contains("메뉴"))
                .findFirst()
                .orElseThrow(RuntimeException::new);

        menuButton.click();

        // 메뉴 type1, type2
        List<WebElement> type1and2MenuEls = crawling.getDriver().findElements(By.cssSelector(type1and2Menus));
        for (WebElement type1and2MenuEl : type1and2MenuEls) {
            RestaurantMenu restaurantMenu = new RestaurantMenu();
            // 메뉴 이름
            restaurantMenu.setMenuName(crawling.getTextBySelectorInWebElement(type1and2MenuName, type1and2MenuEl));
            // 메뉴 가격
            String priceText = crawling.getTextBySelectorInWebElement(type1and2MenuPrice, type1and2MenuEl);
            String price = priceText.replaceAll("[^0-9,]", "") + "원";
            restaurantMenu.setMenuPrice(price);
            // 메뉴 이미지
            if (crawling.isElementExist(type1and2isMenuImgExist)) {
                restaurantMenu.setNaverType("type1");
                try {
                    String imgUrl = crawling.getAttributeInWebElement(type1and2MenuImgUrl, "src", type1and2MenuEl);
                    restaurantMenu.setMenuImgUrl(imgUrl);
                } catch (Exception e) {
                    restaurantMenu.setMenuImgUrl("icon");
                }
            } else {
                restaurantMenu.setNaverType("type2");
                restaurantMenu.setMenuImgUrl("no_img");
            }

            log.info("[새로운 menu]\n식당id: {}, 메뉴 이름: {}, 메뉴 가격: {}, 메뉴 이미지 url: {}",
                    restaurant.getRestaurantId(), restaurantMenu.getMenuName(), restaurantMenu.getMenuPrice(), restaurantMenu.getMenuImgUrl());

//            restaurantMenuRepository.save(restaurantMenu);
        }

        // 메뉴 type3, type4
        List<WebElement> type3and4MenuEls = crawling.getDriver().findElements(By.cssSelector(type3and4Menus));
        for (WebElement type3and4MenuEl : type3and4MenuEls) {
            RestaurantMenu restaurantMenu = new RestaurantMenu();
            // 메뉴 이름
            restaurantMenu.setMenuName(crawling.getTextBySelectorInWebElement(type3and4MenuName, type3and4MenuEl));
            // 메뉴 가격
            String priceText = crawling.getTextBySelectorInWebElement(type3and4MenuPrice, type3and4MenuEl);
            String price = priceText.replaceAll("[^0-9,]", "") + "원";
            restaurantMenu.setMenuPrice(price);
            // 메뉴 이미지
            if (crawling.isElementExist(type3and4isMenuImgExist)) {
                restaurantMenu.setNaverType("type3");
                WebElement imgElement = crawling.getElementBySelectorInWebElement(type3and4MenuImgUrl, type3and4MenuEl);
                String imgHtmlContent = imgElement.getAttribute("outerHTML");
                Pattern urlPattern = Pattern.compile("\\((.*?)\\)");
                Matcher matcher = urlPattern.matcher(imgHtmlContent);
                if (matcher.find()) {
                    // URL에서 필요한 부분만 추출
                    restaurantMenu.setMenuImgUrl(matcher.group(1).substring(6, matcher.group(1).length() - 1));
                } else if (imgElement.getAttribute("src") != null) {
                    restaurantMenu.setMenuImgUrl(imgElement.getAttribute("src"));
                } else {
                    log.error("{} id 레스토랑 메뉴 크롤링 중 오류 발생.\n[메뉴 element 내용]\n{}", restaurant.getRestaurantId(), type3and4MenuEl);
                    throw new RuntimeException();
                }
            } else {
                restaurantMenu.setNaverType("type4");
                restaurantMenu.setMenuImgUrl("no_img");
            }

            log.info("[새로운 menu]\n식당id: {}, 메뉴 이름: {}, 메뉴 가격: {}, 메뉴 이미지 url: {}",
                    restaurant.getRestaurantId(), restaurantMenu.getMenuName(), restaurantMenu.getMenuPrice(), restaurantMenu.getMenuImgUrl());

            // restaurantMenuRepository.save(restaurantMenu);
        }
    }
}
