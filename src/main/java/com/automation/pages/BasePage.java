package com.automation.pages;

import com.automation.utils.DriverManager;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * BasePage provides common Playwright actions used by all Page Objects.
 * Every Page Object should extend this class.
 */
public abstract class BasePage {

    protected final Page page;

    protected BasePage() {
        this.page = DriverManager.getPage();
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    public void navigateTo(String url) {
        page.navigate(url);
    }

    public String getPageTitle() {
        return page.title();
    }

    public String getCurrentUrl() {
        return page.url();
    }

    // -------------------------------------------------------------------------
    // Interactions
    // -------------------------------------------------------------------------

    public void click(String selector) {
        page.locator(selector).click();
    }

    public void fill(String selector, String text) {
        page.locator(selector).fill(text);
    }

    public void selectOption(String selector, String value) {
        page.locator(selector).selectOption(value);
    }

    public void clearAndFill(String selector, String text) {
        Locator locator = page.locator(selector);
        locator.clear();
        locator.fill(text);
    }

    // -------------------------------------------------------------------------
    // Assertions / State
    // -------------------------------------------------------------------------

    public boolean isVisible(String selector) {
        return page.locator(selector).isVisible();
    }

    public String getText(String selector) {
        return page.locator(selector).innerText();
    }

    public String getAttributeValue(String selector, String attribute) {
        return page.locator(selector).getAttribute(attribute);
    }

    // -------------------------------------------------------------------------
    // Waits
    // -------------------------------------------------------------------------

    public void waitForVisible(String selector) {
        page.locator(selector).waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    public void waitForHidden(String selector) {
        page.locator(selector).waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
    }

    public void waitForUrl(String urlPattern) {
        page.waitForURL(urlPattern);
    }

    // -------------------------------------------------------------------------
    // Screenshots (attached to Allure reports via Hooks)
    // -------------------------------------------------------------------------

    public byte[] takeScreenshot() {
        return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
    }
}
