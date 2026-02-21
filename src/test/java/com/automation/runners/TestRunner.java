package com.automation.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TestRunner is the entry point for running all Cucumber tests.
 *
 * Key configuration options:
 *   FILTER_TAGS_PROPERTY_NAME  - run only scenarios tagged with a specific tag
 *                                e.g. "@smoke" or "@regression and not @wip"
 *   GLUE_PROPERTY_NAME         - packages where step definitions and hooks live
 *   PLUGIN_PROPERTY_NAME       - output plugins (Allure, pretty console output, etc.)
 *
 * To run a specific tag from the command line:
 *   mvn test -Dcucumber.filter.tags="@smoke"
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME,   value = "com.automation")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm, pretty")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @wip")
public class TestRunner {
    // This class is intentionally empty.
    // Its annotations configure the Cucumber test engine.
}
