package com.automation.hooks;

import com.automation.utils.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Hooks run before/after each scenario.
 * - @Before : starts the browser (passes scenario name for video/HAR file naming)
 * - @After  : attaches screenshot on failure, video and HAR if enabled, then closes the browser
 */
public class Hooks {

    @Before(order = 1)
    public void setUp(Scenario scenario) {
        DriverManager.initDriver(scenario.getName());
    }

    @After(order = 1)
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            attachScreenshot("Failure Screenshot");
        }

        // Close browser first — Playwright finalises video/HAR files on context close
        DriverManager.quitDriver();

        attachVideo();
        attachHar(scenario.getName());
    }

    // -------------------------------------------------------------------------
    // Attachment helpers
    // -------------------------------------------------------------------------

    private void attachScreenshot(String name) {
        try {
            byte[] screenshot = DriverManager.getPage().screenshot();
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), "png");
        } catch (Exception e) {
            // Page may already be closed — silently skip
        }
    }

    private void attachVideo() {
        Path videoPath = DriverManager.getVideoPath();
        if (videoPath == null) return;

        try {
            // Playwright writes the video with a generated UUID filename — find it
            Path videoDir = videoPath.getParent();
            if (!Files.exists(videoDir)) return;

            Path video = Files.list(videoDir)
                    .filter(p -> p.toString().endsWith(".webm"))
                    .max(java.util.Comparator.comparingLong(p -> p.toFile().lastModified()))
                    .orElse(null);

            if (video != null && Files.exists(video)) {
                try (InputStream is = Files.newInputStream(video)) {
                    Allure.addAttachment("Video Recording", "video/webm", is, "webm");
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to attach video: " + e.getMessage());
        }
    }

    private void attachHar(String scenarioName) {
        Path harPath = DriverManager.getHarPath();
        if (harPath == null || !Files.exists(harPath)) return;

        try (InputStream is = Files.newInputStream(harPath)) {
            Allure.addAttachment("Network (HAR) - " + scenarioName, "application/json", is, "json");
        } catch (Exception e) {
            System.err.println("Failed to attach HAR: " + e.getMessage());
        }
    }
}
