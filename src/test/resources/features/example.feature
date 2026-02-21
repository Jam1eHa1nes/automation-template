@regression
Feature: Todo App
  As a user
  I want to manage my todo list
  So that I can track my tasks

  Background:
    Given I am on the Todo app

  @smoke
  Scenario: Add a new todo
    When I add a todo "Buy groceries"
    Then the todo "Buy groceries" should be visible
    And there should be 1 todo(s) remaining

  @smoke
  Scenario: Complete a todo
    When I add a todo "Walk the dog"
    And I complete the todo "Walk the dog"
    Then the todo "Walk the dog" should be completed
    And there should be 0 todo(s) remaining

  @smoke
  Scenario: Delete a todo
    When I add a todo "Do the laundry"
    And I delete the todo "Do the laundry"
    Then the todo "Do the laundry" should not be visible

  Scenario Outline: Add multiple todos
    When I add a todo "<task>"
    Then the todo "<task>" should be visible

    Examples:
      | task            |
      | Read a book     |
      | Go for a run    |

  @wip
  Scenario: Work in progress - skipped by default
    When I add a todo "WIP task"
    Then the todo "WIP task" should be visible
