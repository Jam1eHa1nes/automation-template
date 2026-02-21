@regression
Feature: Universal Steps Demo
  Demonstrates universal steps using the ElementRegistry.
  Element names resolve to selectors automatically — no raw selectors or quotes needed.

  @smoke
  Scenario: Navigate to a page and assert the title
    Given I navigate to "https://demo.playwright.dev/todomvc"
    Then the page title should contain "React"
    And the URL should contain "todomvc"

  @smoke
  Scenario: Interact with elements using registry names
    Given I navigate to "https://demo.playwright.dev/todomvc"
    When I type "Buy milk" into TODO_INPUT
    And I press the "Enter" key
    Then TODO_LIST should be visible
    And TODO_LIST should contain the text "Buy milk"

  @smoke
  Scenario: Assert element visibility
    Given I navigate to "https://demo.playwright.dev/todomvc"
    Then TODO_INPUT should be visible
    And TODO_COUNT should not be visible
