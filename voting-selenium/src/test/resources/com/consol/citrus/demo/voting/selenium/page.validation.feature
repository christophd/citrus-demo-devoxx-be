Feature: Voting pages

  Background:
    Given page "welcomePage" com.consol.citrus.demo.voting.selenium.pages.WelcomePage
    Given page "votingListPage" com.consol.citrus.demo.voting.selenium.pages.VotingListPage
    Given URL: http://localhost:8080/rest/services
    Given send DELETE /voting
    And receive status 200 OK

  Scenario: Welcome page
    When user starts browser
    And user navigates to "http://localhost:8080"
    Then page welcomePage should validate

  Scenario: Start application
    When user navigates to "http://localhost:8080"
    And page welcomePage performs startApp
    And sleep 500 ms
    Then page votingListPage should validate

  Scenario: Add voting
    Given user navigates to "http://localhost:8080/voting"
    When page votingListPage performs submit with arguments
    | Do you like Belgian beer? |
    And sleep 500 ms
    Then page should display element with link-text="Do you like Belgian beer?"
    And page votingListPage should validate

  Scenario: Add voting with options
    Given user navigates to "http://localhost:8080/voting"
    When page votingListPage performs submit with arguments
      | What is your favorite beer? |
      | Stella:Leffe:Corsendonk:Duvel:Westmalle |
    And sleep 500 ms
    Then page should display element with link-text="What is your favorite beer?"
    And page votingListPage should validate