Feature: Show voting results

  As a user I want to vote for an option. All voting results are stored
  and the user should be able to get top vote option for each voting.

  Background:
    Given I create new voting "Do you like Devoxx catering?"
    And voting options are "yes:no"

  Scenario: Initial vote results
    Then votes should be
      | yes | 0 |
      | no  | 0 |

  Scenario: Get vote results
    When I vote for "yes"
    Then votes should be
      | yes | 1 |
      | no  | 0 |
    And top vote should be "yes"

  Scenario: Get top vote result
    When I vote for "yes" 3 times
    And I vote for "no" 5 times
    And votes should be
      | yes | 3 |
      | no  | 5 |
    Then top vote should be "no"
