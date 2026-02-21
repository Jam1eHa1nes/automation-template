package com.automation.steps;

import com.automation.pages.ExamplePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

/**
 * ExampleSteps contains step definitions that map Gherkin steps
 * in the feature files to Java actions on page objects.
 *
 * Keep step definitions thin — all interaction logic belongs in the Page Object.
 */
public class ExampleSteps {

    private final ExamplePage examplePage = new ExamplePage();

    @Given("I am on the Todo app")
    public void iAmOnTheTodoApp() {
        examplePage.goToTodoApp();
    }

    @When("I add a todo {string}")
    public void iAddATodo(String text) {
        examplePage.addTodo(text);
    }

    @When("I complete the todo {string}")
    public void iCompleteTheTodo(String text) {
        examplePage.completeTodo(text);
    }

    @When("I delete the todo {string}")
    public void iDeleteTheTodo(String text) {
        examplePage.deleteTodo(text);
    }

    @Then("the todo {string} should be visible")
    public void theTodoShouldBeVisible(String text) {
        Assertions.assertTrue(examplePage.isTodoVisible(text),
                "Expected todo '" + text + "' to be visible");
    }

    @Then("the todo {string} should be completed")
    public void theTodoShouldBeCompleted(String text) {
        Assertions.assertTrue(examplePage.isTodoCompleted(text),
                "Expected todo '" + text + "' to be marked as completed");
    }

    @Then("there should be {int} todo\\(s) remaining")
    public void thereShouldBeTodosRemaining(int count) {
        String itemsLeft = examplePage.getItemsLeftText();
        Assertions.assertEquals(String.valueOf(count), itemsLeft,
                "Expected " + count + " items left but found: " + itemsLeft);
    }

    @Then("the todo {string} should not be visible")
    public void theTodoShouldNotBeVisible(String text) {
        Assertions.assertFalse(examplePage.isTodoVisible(text),
                "Expected todo '" + text + "' to be gone");
    }
}
