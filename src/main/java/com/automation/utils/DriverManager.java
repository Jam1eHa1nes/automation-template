package com.automation.utils;

import com.microsoft.playwright.*;

/**
 * DriverManager manages the lifecycle of Playwright browser instances.
 * Uses ThreadLocal to support parallel test execution safely.
 */
public class DriverManager {

    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Browser>   browserThreadLocal     = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page>      pageThreadLocal        = new ThreadLocal<>();

    private DriverManager() {}

    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    public static void initDriver() {
        String browserName  = ConfigManager.get("browser", "chromium");
        boolean headless    = Boolean.parseBoolean(ConfigManager.get("headless", "false"));

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

        BrowserContext context = browser.newContext(
                new Browser.NewContextOptions()
                        .setViewportSize(1920, 1080));
        contextThreadLocal.set(context);

        pageThreadLocal.set(context.newPage());
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

    // -------------------------------------------------------------------------
    // Teardown
    // -------------------------------------------------------------------------

    public static void quitDriver() {
        if (pageThreadLocal.get() != null) {
            pageThreadLocal.get().close();
            pageThreadLocal.remove();
        }
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
