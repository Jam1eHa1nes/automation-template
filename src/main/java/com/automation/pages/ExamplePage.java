package com.automation.pages;

/**
 * ExamplePage demonstrates the Page Object Model pattern against
 * Playwright's own TodoMVC demo app — a reliable, bot-friendly target.
 * Replace this with your own page objects, keeping selectors and
 * actions encapsulated here — away from step definitions.
 */
public class ExamplePage extends BasePage {

    private static final String BASE_URL      = "https://demo.playwright.dev/todomvc";
    private static final String TODO_INPUT    = ".new-todo";
    private static final String TODO_ITEMS    = ".todo-list li";
    private static final String TODO_ITEM_BY_TEXT = ".todo-list li:has-text('%s')";
    private static final String ITEMS_LEFT    = ".todo-count strong";
    private static final String COMPLETE_ALL  = ".toggle-all";

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    public void goToTodoApp() {
        navigateTo(BASE_URL);
        waitForVisible(TODO_INPUT);
    }

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    public void addTodo(String text) {
        fill(TODO_INPUT, text);
        page.keyboard().press("Enter");
    }

    public void completeTodo(String text) {
        String toggleSelector = String.format(".todo-list li:has-text('%s') .toggle", text);
        click(toggleSelector);
    }

    public void deleteTodo(String text) {
        String itemSelector  = String.format(".todo-list li:has-text('%s')", text);
        String deleteSelector = String.format(".todo-list li:has-text('%s') .destroy", text);
        page.locator(itemSelector).hover();
        click(deleteSelector);
    }

    // -------------------------------------------------------------------------
    // Assertions / State
    // -------------------------------------------------------------------------

    public boolean isTodoVisible(String text) {
        return isVisible(String.format(TODO_ITEM_BY_TEXT, text));
    }

    public int getTodoCount() {
        return page.locator(TODO_ITEMS).count();
    }

    public String getItemsLeftText() {
        return getText(ITEMS_LEFT);
    }

    public boolean isTodoCompleted(String text) {
        String itemSelector = String.format(".todo-list li:has-text('%s')", text);
        String classes = getAttributeValue(itemSelector, "class");
        return classes != null && classes.contains("completed");
    }
}
