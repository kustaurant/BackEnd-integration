package com.kustaurant.restauranttier.crawling;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Crawling {
    @Getter
    private WebDriver driver = null;
    private WebDriverWait wait = null;
    private Actions actions = null;

    public Crawling() {
        // Setup ChromeOptions
        ChromeOptions options = new ChromeOptions();
        // Create ChromeDriver instance
        this.driver = new ChromeDriver(options);
    }

    public Crawling maximizeWindow() {
        this.driver.manage().window().maximize();
        return this;
    }

    public void openUrl(String url) {
        this.driver.get(url);
    }

    public void quitDriver() {
        this.driver.quit();
    }

    // wait 시간(초) 설정
    public Crawling setWaitTime(int sec) {
        try {
            this.wait = new WebDriverWait(driver, Duration.ofSeconds(sec));
        } catch (TimeoutException e) {
            System.out.println("ERROR: 시간 초과 - " + e.getLocalizedMessage());
        }
        return this;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void changeIframe(String iframeSelector) {
        // iframe 요소 기다리기
        try {
            WebElement iframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(iframeSelector)));
            // iframe으로 전환
            log.info("iframe 전환됨");
            driver.switchTo().frame(iframe);
        } catch (Exception e) {
            log.error("iframe 전환 중에 예외 발생", e);
            throw new RuntimeException();
        }
    }

    public boolean isElementExist(String selector) {
        try {
            this.wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
            return driver.findElement(By.cssSelector(selector)) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public WebElement getElementBySelector(String selector) {
        try {
            this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
            return driver.findElement(By.cssSelector(selector));
        } catch (Exception e) {
            log.error("\"{}\" selector를 읽는 시도 중에 예외 발생", selector, e);
            throw new RuntimeException();
        }
    }

    public WebElement getElementBySelectorInWebElement(String selector, WebElement element) {
        try {
            return element.findElement(By.cssSelector(selector));
        } catch (Exception e) {
            log.error("\"{}\" selector를 읽는 시도 중에 예외 발생", selector, e);
            throw new RuntimeException();
        }
    }

    public List<WebElement> getElementsBySelector(String selector) {
        try {
            this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
            return driver.findElements(By.cssSelector(selector));
        } catch (Exception e) {
            log.error("\"{}\" selector를 읽는 시도 중에 예외 발생", selector, e);
            throw new RuntimeException();
        }
    }

    public List<WebElement> getElementsBySelectorInWebElement(String selector, WebElement element) {
        try {
            return element.findElements(By.cssSelector(selector));
        } catch (Exception e) {
            log.error("\"{}\" selector를 읽는 시도 중에 예외 발생", selector, e);
            throw new RuntimeException();
        }
    }

    public String getTextBySelector(String selector) {
        try {
            this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
            return driver.findElement(By.cssSelector(selector)).getText();
        } catch (Exception e) {
            log.error("\"{}\" selector의 text를 읽는 시도 중에 예외 발생", selector, e);
            throw new RuntimeException();
        }
    }

    public String getTextBySelectorInWebElement(String selector, WebElement element) {
        try {
            return element.findElement(By.cssSelector(selector)).getText();
        } catch (Exception e) {
            log.error("\"{}\" selector의 text를 읽는 시도 중에 예외 발생", selector, e);
            throw new RuntimeException();
        }
    }

    public String getAttribute(String selector, String attributeName) {
        try {
            this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
            return driver.findElement(By.cssSelector(selector)).getAttribute(attributeName);
        } catch (Exception e) {
            log.error("\"{}\" selector의 img src를 읽는 시도 중에 예외 발생", selector, e);
            throw new RuntimeException();
        }
    }

    public String getAttributeInWebElement(String selector, String attributeName, WebElement element) {
        try {
            this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
            return element.findElement(By.cssSelector(selector)).getAttribute(attributeName);
        } catch (Exception e) {
            log.error("\"{}\" selector의 img src를 읽는 시도 중에 예외 발생", selector, e);
            throw new RuntimeException();
        }
    }

    public String[] getLonAndLat() {
        try {
            String[] result = new String[]{"", ""};

            // 페이지에서 모든 <script> 태그 찾기
            List<WebElement> scriptTags = driver.findElements(By.tagName("script"));

            // 패턴 정의: lon과 lat 값을 찾기 위한 정규 표현식
            Pattern pattern = Pattern.compile("\"lon\":\"(\\d+\\.\\d+)\",\"lat\":\"(\\d+\\.\\d+)\"");

            // 각 <script> 태그에서 코드 추출
            for (WebElement scriptTag : scriptTags) {
                String scriptContent = scriptTag.getAttribute("innerHTML");

                // 정규 표현식으로 데이터 추출
                Matcher matcher = pattern.matcher(scriptContent);
                while (matcher.find()) {
                    String lon = matcher.group(1);
                    result[0] = lon;
                    String lat = matcher.group(2);
                    result[1] = lat;
                    System.out.println("Longitude: " + lon + ", Latitude: " + lat);
                }
            }
            return result;
        } catch (Exception e) {
            log.error("script를 읽는 도중에 오류 발생", e);
            throw new RuntimeException();
        }
    }
}
