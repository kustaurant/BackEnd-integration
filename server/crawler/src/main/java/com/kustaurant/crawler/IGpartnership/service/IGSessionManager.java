package com.kustaurant.crawler.IGpartnership;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class IGSessionManager {

    private static final String INSTAGRAM_BASE_URL = "https://www.instagram.com";
    private static final Path STATE_PATH = Paths.get(System.getProperty("user.dir"), "ig_state.json");

    @Value("${instagram.username}")
    private String id;

    @Value("${instagram.password}")
    private String pw;

    public BrowserContext createContext(Browser browser) {
        boolean firstRun = !Files.exists(STATE_PATH);

        Browser.NewContextOptions ctxOpt = new Browser.NewContextOptions()
                .setViewportSize(1200, 720)
                .setLocale("ko-KR")
                .setUserAgent(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/122.0.0.0 Safari/537.36"
                );

        if (!firstRun) {
            ctxOpt.setStorageStatePath(STATE_PATH);
            log.info("Loaded storageState from {}", STATE_PATH.toAbsolutePath());
        }

        BrowserContext context = browser.newContext(ctxOpt);

        if (firstRun) {
            log.info("First run -> doing login & save state");
            doLoginAndSaveState(context);
            log.info("State saved -> {}", STATE_PATH.toAbsolutePath());
        }

        return context;
    }

    private void doLoginAndSaveState(BrowserContext context) {
        if (id == null || id.isBlank() || pw == null || pw.isBlank()) {
            throw new IllegalStateException("instagram.username / instagram.password 값이 비어 있음");
        }

        Page loginPage = context.newPage();

        loginPage.navigate(INSTAGRAM_BASE_URL + "/accounts/login/",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        loginPage.waitForTimeout(5000);

        Locator usernameInput = loginPage.locator(
                "input[name='email'], input[autocomplete*='username'], input[type='text']"
        ).first();

        Locator passwordInput = loginPage.locator(
                "input[name='pass'], input[autocomplete*='password'], input[type='password']"
        ).first();

        usernameInput.waitFor(new Locator.WaitForOptions().setTimeout(10000));
        passwordInput.waitFor(new Locator.WaitForOptions().setTimeout(10000));

        usernameInput.fill(id);
        passwordInput.fill(pw);

        loginPage.keyboard().press("Enter");
        loginPage.waitForTimeout(7000);

        context.storageState(new BrowserContext.StorageStateOptions()
                .setPath(STATE_PATH));

        loginPage.close();
    }
}