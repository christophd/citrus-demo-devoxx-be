Feature: Close voting

  As a user I want to close a voting in order to stop accepting votes.

  Scenario: Closed voting should not accept votes
    Given New default voting
    When voting is closed
    And I vote for "yes"
    Then I should get the error "Failed to add vote - voting is closed!"
    And votes of option "yes" should be 0
    And votes of option "no" should be 0