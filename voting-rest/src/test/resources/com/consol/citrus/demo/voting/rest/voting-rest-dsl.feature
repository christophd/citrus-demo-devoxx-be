Feature: Voting Http REST API

  Background:
    Given Voting list is empty
    And New voting "Do you like Belgian beer?"
    And voting options are "yes:no"

  Scenario: Create voting
    When client creates the voting
    Then client should be able to get the voting
    And the list of votings should contain "Do you like Belgian beer?"

  Scenario: Add votes
    When client creates the voting
    And client votes for "yes"
    Then votes should be
      | yes | 1 |
      | no  | 0 |

  Scenario: Top vote
    When client creates the voting
    And client votes for "no"
    Then votes should be
      | yes | 0 |
      | no  | 1 |
    And top vote should be "no"

  Scenario: Close voting
    Given reporting is enabled
    When client creates the voting
    And client votes for "yes" 3 times
    And client votes for "no" 2 times
    And client closes the voting
    Then participants should receive reporting mail
"""
Dear participants,

the voting '${title}' came to an end.

The top answer is 'yes'!

Have a nice day!
Your Voting-App Team
"""