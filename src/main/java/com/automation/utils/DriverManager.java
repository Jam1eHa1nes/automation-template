package com.automation.utils;

import com.microsoft.playwright.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * DriverManager manages the lifecycle of Playwright browser instances.
 * Uses ThreadLocal to support parallel test execution safely.
 *
 * Supports optional video recording and failed network call capture,
 * controlled via config.properties:
 *   video.enabled=true
 *   network.failures.enabled=true
 */
public class DriverManager {

    private static final ThreadLocal<Playwright>     playwrightThreadLocal      = new ThreadLocal<>();
    private static final ThreadLocal<Browser>        browserThreadLocal         = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThreadLocal         = new ThreadLocal<>();
    private static final ThreadLocal<Page>           pageThreadLocal            = new ThreadLocal<>();
    private static final ThreadLocal<Path>           videoPathThreadLocal       = new ThreadLocal<>();
    private static final ThreadLocal<List<String>>   failedNetworkThreadLocal   = new ThreadLocal<>();

    private DriverManager() {}

    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    public static void initDriver(String scenarioName) {
        String  browserName      = ConfigManager.get("browser", "chromium");
        boolean headless         = Boolean.parseBoolean(ConfigManager.get("headless", "false"));
        boolean videoEnabled     = Boolean.parseBoolean(ConfigManager.get("video.enabled", "false"));
        boolean networkEnabled   = Boolean.parseBoolean(ConfigManager.get("network.failures.enabled", "false"));

        Playwright playwright = Playwright.create();
        playwrightThreadLocal.set(playwright);

        Browser browser = switch (browserName.toLowerCase()) {
            case "firefox" -> playwright.firefox().launch(
                    new BrowserType.LaunchOptions().setHeadless(headless));
            case "webkit"  -> playwright.webkit().launch(
                    new BrowserType.LaunchOptions().setHeadless(headless));
            default        -> playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(headless));
        };
        browserThreadLocal.set(browser);

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(1920, 1080);

        if (videoEnabled) {
            Path videoDir = Paths.get("target/videos");
            videoDir.toFile().mkdirs();
            contextOptions.setRecordVideoDir(videoDir)
                          .setRecordVideoSize(1920, 1080);
            videoPathThreadLocal.set(videoDir.resolve(
                    scenarioName.replaceAll("[^a-zA-Z0-9]", "_") + ".webm"));
        }

        BrowserContext context = browser.newContext(contextOptions);
        contextThreadLocal.set(context);

        Page page = context.newPage();

        if (networkEnabled) {
            List<String> failedCalls = new ArrayList<>();
            failedNetworkThreadLocal.set(failedCalls);

            page.onResponse(response -> {
                int status = response.status();
                if (status >= 400) {
                    String entry = String.format("[%d] %s %s",
                            status, response.request().method(), response.url());
                    failedCalls.add(entry);
                    System.out.printf("[NETWORK FAILURE] %s%n", entry);
                }
            });
        }

        pageThreadLocal.set(page);
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public static Page getPage() {
        return pageThreadLocal.get();
    }

    public static BrowserContext getContext() {
        return contextThreadLocal.get();
    }

    /**
     * Returns the path where the recorded video will be saved, or null if
     * video recording is disabled.
     * Must be called AFTER quitDriver() — Playwright finalises the file on context close.
     */
    public static Path getVideoPath() {
        return videoPathThreadLocal.get();
    }

    /**
     * Returns the list of failed network calls (4xx/5xx) captured during the scenario,
     * or null if network failure capture is disabled.
     */
    public static List<String> getFailedNetworkCalls() {
        return failedNetworkThreadLocal.get();
    }

    // -------------------------------------------------------------------------
    // Teardown
    // -------------------------------------------------------------------------

    public static void quitDriver() {
        if (pageThreadLocal.get() != null) {
            pageThreadLocal.get().close();
            pageThreadLocal.remove();
        }
        // Context must close before video file is finalised
        if (contextThreadLocal.get() != null) {
            contextThreadLocal.get().close();
            contextThreadLocal.remove();
        }
        if (browserThreadLocal.get() != null) {
            browserThreadLocal.get().close();
            browserThreadLocal.remove();
        }
        if (playwrightThreadLocal.get() != null) {
            playwrightThreadLocal.get().close();
            playwrightThreadLocal.remove();
        }
        failedNetworkThreadLocal.remove();
    }
}
