package com.automation.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * ElementRegistry maps human-readable element names to Playwright selectors.
 *
 * Used by UniversalSteps so that feature files remain readable and selector-free:
 *
 *   When I click on "SUBMIT"          (instead of ".btn[type='submit']")
 *   Then "ERROR MESSAGE" should be visible  (instead of ".error-msg")
 *
 * HOW TO ADD ELEMENTS:
 *   1. Add a new entry to the ELEMENTS map below.
 *   2. Use UPPER_SNAKE_CASE for the key — this is what goes in the feature file.
 *   3. The value is any valid Playwright selector (CSS, text=, xpath=, etc.)
 *
 * SELECTOR TYPES (Playwright):
 *   CSS       ".my-class", "#my-id", "button[type='submit']"
 *   Text      "text=Sign in"
 *   XPath     "xpath=//button[@id='x']"
 *   Role      "role=button[name='Submit']"
 */
public class ElementRegistry {

    private static final Map<String, String> ELEMENTS = new HashMap<>();

    static {
        // ---------------------------------------------------------------
        // TODO APP (demo) — replace with your own application's elements
        // ---------------------------------------------------------------
        ELEMENTS.put("TODO_INPUT",       ".new-todo");
        ELEMENTS.put("TODO_LIST",        ".todo-list");
        ELEMENTS.put("TODO_COUNT",       ".todo-count");
        ELEMENTS.put("COMPLETE_ALL",     ".toggle-all");

        // ---------------------------------------------------------------
        // COMMON — elements likely to appear across multiple pages
        // ---------------------------------------------------------------
        ELEMENTS.put("SUBMIT",           "button[type='submit']");
        ELEMENTS.put("CANCEL",           "button[type='button']");
        ELEMENTS.put("CONFIRM",          "text=Confirm");
        ELEMENTS.put("CLOSE",            "text=Close");
        ELEMENTS.put("SEARCH_INPUT",     "input[type='search']");
        ELEMENTS.put("EMAIL_INPUT",      "input[type='email']");
        ELEMENTS.put("PASSWORD_INPUT",   "input[type='password']");
        ELEMENTS.put("ERROR_MESSAGE",    ".error-message");
        ELEMENTS.put("SUCCESS_MESSAGE",  ".success-message");
        ELEMENTS.put("LOADING_SPINNER",  ".loading-spinner");
        ELEMENTS.put("PAGE_HEADING",     "h1");
    }

    private ElementRegistry() {}

    /**
     * Resolves a name from the registry to its Playwright selector.
     *
     * If the name is not found in the registry it is returned as-is,
     * allowing raw selectors to still be used in feature files if needed.
     *
     * @param name the element name (e.g. "SUBMIT") or a raw selector
     * @return the corresponding Playwright selector
     * @throws IllegalArgumentException if the name looks like a key (all uppercase)
     *                                  but has no registered entry
     */
    public static String resolve(String name) {
        String key = name.toUpperCase().trim();

        if (ELEMENTS.containsKey(key)) {
            return ELEMENTS.get(key);
        }

        // If the input looks like a registry key (all caps / spaces) but wasn't
        // found, throw a helpful error rather than silently treating it as a selector
        if (name.equals(name.toUpperCase())) {
            throw new IllegalArgumentException(
                    "No element registered for key: '" + name + "'. " +
                    "Add it to ElementRegistry or check for a typo.");
        }

        // Otherwise treat it as a raw Playwright selector
        return name;
    }

    /**
     * Registers a new element at runtime (useful for dynamic page elements in hooks).
     */
    public static void register(String name, String selector) {
        ELEMENTS.put(name.toUpperCase().trim(), selector);
    }
}
