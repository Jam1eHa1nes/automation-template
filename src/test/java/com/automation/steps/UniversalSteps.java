package com.automation.steps;

import com.automation.utils.DriverManager;
import com.automation.utils.ElementRegistry;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

/**
 * UniversalSteps provides generic step definitions that can be used in any
 * feature file, regardless of the page or application under test.
 *
 * Element names in steps are resolved through ElementRegistry, so feature
 * files use readable names instead of raw selectors:
 *
 *   When I click on "SUBMIT"               ✅ readable
 *   When I click on "button[type=submit]"  ❌ avoid
 *
 * Add new element mappings in: src/main/java/com/automation/utils/ElementRegistry.java
 */
public class UniversalSteps {

    private Page page() {
        return DriverManager.getPage();
    }

    private Locator locate(String name) {
        return page().locator(ElementRegistry.resolve(name));
    }

    // =========================================================================
    // NAVIGATION
    // =========================================================================

    @Given("I navigate to {string}")
    public void iNavigateTo(String url) {
        page().navigate(url);
    }

    @When("I refresh the page")
    public void iRefreshThePage() {
        page().reload();
    }

    @When("I go back")
    public void iGoBack() {
        page().goBack();
    }

    @When("I go forward")
    public void iGoForward() {
        page().goForward();
    }

    // =========================================================================
    // INTERACTIONS
    // =========================================================================

    @When("I click on {word}")
    public void iClickOn(String name) {
        locate(name).click();
    }

    @When("I click on the text {string}")
    public void iClickOnTheText(String text) {
        page().getByText(text).click();
    }

    @When("I type {string} into {word}")
    public void iTypeInto(String text, String name) {
        locate(name).fill(text);
    }

    @When("I clear and type {string} into {word}")
    public void iClearAndTypeInto(String text, String name) {
        Locator locator = locate(name);
        locator.clear();
        locator.fill(text);
    }

    @When("I press the {string} key")
    public void iPressTheKey(String key) {
        page().keyboard().press(key);
    }

    @When("I hover over {word}")
    public void iHoverOver(String name) {
        locate(name).hover();
    }

    @When("I select {string} from {word}")
    public void iSelectFrom(String value, String name) {
        locate(name).selectOption(value);
    }

    @When("I scroll to {word}")
    public void iScrollTo(String name) {
        locate(name).scrollIntoViewIfNeeded();
    }

    // =========================================================================
    // ASSERTIONS
    // =========================================================================

    @Then("the page title should be {string}")
    public void thePageTitleShouldBe(String expected) {
        Assertions.assertEquals(expected, page().title(),
                "Page title mismatch");
    }

    @Then("the page title should contain {string}")
    public void thePageTitleShouldContain(String expected) {
        Assertions.assertTrue(page().title().contains(expected),
                "Expected page title to contain '" + expected + "' but was: " + page().title());
    }

    @Then("the URL should be {string}")
    public void theUrlShouldBe(String expected) {
        Assertions.assertEquals(expected, page().url(),
                "URL mismatch");
    }

    @Then("the URL should contain {string}")
    public void theUrlShouldContain(String expected) {
        Assertions.assertTrue(page().url().contains(expected),
                "Expected URL to contain '" + expected + "' but was: " + page().url());
    }

    @Then("{word} should be visible")
    public void shouldBeVisible(String name) {
        String selector = ElementRegistry.resolve(name);
        locate(name).waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        Assertions.assertTrue(locate(name).isVisible(),
                "Expected '" + selector + "' to be visible");
    }

    @Then("{word} should not be visible")
    public void shouldNotBeVisible(String name) {
        String selector = ElementRegistry.resolve(name);
        Assertions.assertFalse(locate(name).isVisible(),
                "Expected '" + selector + "' to not be visible");
    }

    @Then("{word} should contain the text {string}")
    public void shouldContainTheText(String name, String expectedText) {
        String selector = ElementRegistry.resolve(name);
        String actual = locate(name).innerText();
        Assertions.assertTrue(actual.contains(expectedText),
                "Expected '" + selector + "' to contain text '" + expectedText + "' but was: " + actual);
    }

    @Then("{word} should have the text {string}")
    public void shouldHaveTheText(String name, String expectedText) {
        String selector = ElementRegistry.resolve(name);
        String actual = locate(name).innerText().trim();
        Assertions.assertEquals(expectedText, actual,
                "Text mismatch for '" + selector + "'");
    }

    @Then("{word} should have attribute {string} with value {string}")
    public void shouldHaveAttributeWithValue(String name, String attribute, String expectedValue) {
        String selector = ElementRegistry.resolve(name);
        String actual = locate(name).getAttribute(attribute);
        Assertions.assertEquals(expectedValue, actual,
                "Expected attribute '" + attribute + "' on '" + selector + "' to be '" + expectedValue + "' but was: " + actual);
    }

    @Then("{word} should be enabled")
    public void shouldBeEnabled(String name) {
        String selector = ElementRegistry.resolve(name);
        Assertions.assertTrue(locate(name).isEnabled(),
                "Expected '" + selector + "' to be enabled");
    }

    @Then("{word} should be disabled")
    public void shouldBeDisabled(String name) {
        String selector = ElementRegistry.resolve(name);
        Assertions.assertFalse(locate(name).isEnabled(),
                "Expected '" + selector + "' to be disabled");
    }
}
