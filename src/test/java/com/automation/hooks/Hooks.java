package com.automation.hooks;

import com.automation.utils.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Hooks run before/after each scenario.
 * - @Before : starts the browser
 * - @After  : attaches screenshot on failure, failed network calls if any,
 *             and video if enabled, then closes the browser
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

        // Close browser first — Playwright finalises the video file on context close
        DriverManager.quitDriver();

        attachFailedNetworkCalls();
        attachVideo();
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

    private void attachFailedNetworkCalls() {
        List<String> failures = DriverManager.getFailedNetworkCalls();
        if (failures == null || failures.isEmpty()) return;

        StringBuilder log = new StringBuilder();
        log.append("Failed Network Calls (4xx / 5xx)\n");
        log.append("=".repeat(50)).append("\n\n");
        failures.forEach(entry -> log.append(entry).append("\n"));

        byte[] bytes = log.toString().getBytes(StandardCharsets.UTF_8);
        Allure.addAttachment("Failed Network Calls", "text/plain",
                new ByteArrayInputStream(bytes), "txt");
    }

    private void attachVideo() {
        Path videoPath = DriverManager.getVideoPath();
        if (videoPath == null) return;

        try {
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
}
