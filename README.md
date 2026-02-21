# Automation Template

A clean BDD automation framework using **Playwright**, **Cucumber**, **Allure**, and **Maven** in Java.

---

## Stack

| Tool        | Version  | Purpose                        |
|-------------|----------|--------------------------------|
| Java        | 17       | Language                       |
| Maven       | 3.x      | Build & dependency management  |
| Playwright  | 1.50.0   | Browser automation             |
| Cucumber    | 7.20.1   | BDD / Gherkin test runner      |
| JUnit 5     | 5.11.4   | Test engine                    |
| Allure      | 2.29.0   | Test reporting                 |

---

## Project Structure

```
src/
├── main/java/com/automation/
│   ├── pages/          # Page Objects (BasePage + one class per page)
│   └── utils/          # DriverManager, ConfigManager
└── test/
    ├── java/com/automation/
    │   ├── hooks/       # Cucumber @Before / @After hooks
    │   ├── runners/     # TestRunner (entry point)
    │   └── steps/       # Step definitions
    └── resources/
        ├── features/    # Gherkin .feature files
        ├── config.properties
        ├── allure.properties
        └── junit-platform.properties
```

---

## Getting Started

### 1. Install Playwright browsers (first time only)
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

### 2. Run all tests
```bash
mvn test
```

### 3. Run a specific tag
```bash
mvn test -Dcucumber.filter.tags="@smoke"
```

### 4. Run headless
```bash
mvn test -Dheadless=true
```

### 5. Switch browser
```bash
mvn test -Dbrowser=firefox
# Options: chromium | firefox | webkit
```

---

## Allure Report (v3)

Open the report after a test run (generates on the fly and opens in browser):
```bash
npx allure open ./target/allure-results
```

Or generate a static HTML report to `allure-report/`:
```bash
npx allure generate ./target/allure-results
```

---

## Adding a New Feature

1. Create a `.feature` file in `src/test/resources/features/`
2. Create a Page Object in `src/main/java/com/automation/pages/` extending `BasePage`
3. Create a step definitions class in `src/test/java/com/automation/steps/`
4. That's it — no runner changes needed, Cucumber picks up new steps automatically
