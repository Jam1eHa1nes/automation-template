package com.automation.hooks;

import com.automation.utils.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;

/**
 * Hooks run before/after each scenario.
 * - @Before : starts the browser
 * - @After  : attaches a screenshot on failure, then closes the browser
 */
public class Hooks {

    @Before(order = 1)
    public void setUp(Scenario scenario) {
        DriverManager.initDriver();
    }

    @After(order = 1)
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            attachScreenshot("Failure Screenshot");
        }
        DriverManager.quitDriver();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void attachScreenshot(String name) {
        try {
            byte[] screenshot = DriverManager.getPage().screenshot();
            Allure.addAttachment(name, new ByteArrayInputStream(screenshot));
        } catch (Exception e) {
            // Page may already be closed — silently skip
        }
    }
}
