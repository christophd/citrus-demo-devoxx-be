Feature: Voting Http JMS

  Scenario Outline: Receive reporting
    Given Voting list is empty
    Given New voting "<question>"
    And voting options are "yes:no"
    And reporting is enabled
    When client creates the voting
    And sleep 250 ms
    And client votes for "yes" <yes_votes> times
    And client votes for "no" <no_votes> times
    And sleep 500 ms
    And client closes the voting
    Then reporting should receive vote results
      | yes | <yes_votes> |
      | no  | <no_votes>  |
    And top vote should be "<top_vote>"

    Examples:
      | question                         | yes_votes | no_votes | top_vote |
      | Did you enjoy the salad?         | 12        | 5        | yes      |
      | Did you enjoy the crab sandwich? | 1         | 25       | no       |
