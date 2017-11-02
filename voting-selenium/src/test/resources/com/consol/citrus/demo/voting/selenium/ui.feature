Feature: Voting user interface

  Background:
    Given URL: http://localhost:8080/rest/services
    Given send DELETE /voting
    And receive status 200 OK

  Scenario: Welcome page
    When user starts browser
    And user navigates to "http://localhost:8080"
    Then page should display link with link-text="Run application"

  Scenario: Start application
    When user navigates to "http://localhost:8080"
    And user clicks link with link-text="Run application"
    And sleep 500 ms
    Then page should display heading with tag-name="h1" having
    | text | Voting list |

    And page should display link with link-text="No voting found"
    And page should display form with id="new-voting" having
    | tag-name  | form          |
    | attribute | method="post" |

  Scenario: Add voting
    Given user navigates to "http://localhost:8080/voting"
    When user sets text "Did you enjoy the conference?" to input with id="title"
    And user clicks button with id="submitNew"
    And sleep 500 ms
    Then page should display element with link-text="Did you enjoy the conference?"

  Scenario: Show voting details
    Given user navigates to "http://localhost:8080/voting"
    And user sets text "Do you like Antwerp?" to input with id="title"
    And user clicks button with id="submitNew"
    And sleep 500 ms
    When user clicks link with link-text="Do you like Antwerp?"
    Then page should display element with tag-name="h2" having
    | text | Do you like Antwerp? |
    And page should display link with link-text="yes"
    And page should display link with link-text="no"
    And page should display link with link-text="Close voting"

  Scenario: Add votes
    Given user navigates to "http://localhost:8080/voting"
    And user sets text "Did you enjoy Devoxx 2017?" to input with id="title"
    And user clicks button with id="submitNew"
    And sleep 500 ms
    When user clicks link with link-text="Did you enjoy Devoxx 2017?"
    And user clicks link with link-text="yes"
    Then page should display element with id="yes" having
    | text | yes: 1 |
    Then page should display element with id="no" having
    | text | no: 0 |