package com.automation.utils;

import com.microsoft.playwright.*;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * DriverManager manages the lifecycle of Playwright browser instances.
 * Uses ThreadLocal to support parallel test execution safely.
 *
 * Supports optional video recording and HAR network capture,
 * controlled via config.properties:
 *   video.enabled=true
 *   network.har.enabled=true
 */
public class DriverManager {

    private static final ThreadLocal<Playwright>     playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Browser>        browserThreadLocal    = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThreadLocal    = new ThreadLocal<>();
    private static final ThreadLocal<Page>           pageThreadLocal       = new ThreadLocal<>();
    private static final ThreadLocal<Path>           videoPathThreadLocal  = new ThreadLocal<>();
    private static final ThreadLocal<Path>           harPathThreadLocal    = new ThreadLocal<>();

    private DriverManager() {}

    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    public static void initDriver(String scenarioName) {
        String  browserName     = ConfigManager.get("browser", "chromium");
        boolean headless        = Boolean.parseBoolean(ConfigManager.get("headless", "false"));
        boolean videoEnabled    = Boolean.parseBoolean(ConfigManager.get("video.enabled", "false"));
        boolean harEnabled      = Boolean.parseBoolean(ConfigManager.get("network.har.enabled", "false"));

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

        String safeName = scenarioName.replaceAll("[^a-zA-Z0-9]", "_");

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(1920, 1080);

        if (videoEnabled) {
            Path videoDir = Paths.get("target/videos");
            videoDir.toFile().mkdirs();
            contextOptions.setRecordVideoDir(videoDir)
                          .setRecordVideoSize(1920, 1080);
            videoPathThreadLocal.set(videoDir.resolve(safeName + ".webm"));
        }

        if (harEnabled) {
            Path harPath = Paths.get("target/har/" + safeName + ".har");
            harPath.getParent().toFile().mkdirs();
            contextOptions.setRecordHarPath(harPath);
            harPathThreadLocal.set(harPath);
        }

        BrowserContext context = browser.newContext(contextOptions);
        contextThreadLocal.set(context);

        Page page = context.newPage();

        if (harEnabled) {
            page.onRequest(request ->
                    System.out.printf("[REQUEST]  %-6s %s%n", request.method(), request.url()));
            page.onResponse(response ->
                    System.out.printf("[RESPONSE] %-4s %s%n", response.status(), response.url()));
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
     * Returns the path to the recorded HAR file, or null if HAR is disabled.
     * Must be called AFTER quitDriver() — Playwright finalises the file on context close.
     */
    public static Path getHarPath() {
        return harPathThreadLocal.get();
    }

    // -------------------------------------------------------------------------
    // Teardown
    // -------------------------------------------------------------------------

    public static void quitDriver() {
        if (pageThreadLocal.get() != null) {
            pageThreadLocal.get().close();
            pageThreadLocal.remove();
        }
        // Context must close before video/HAR files are finalised
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
    }
}
