Feature: Create voting

  As a user I want to create new votings. Each voting is given default vote  options.
  The user should be able to set custom vote options.

  Scenario: Default voting options
    When I create new voting "Did you enjoy the crab sandwich?"
    Then voting title should be "Did you enjoy the crab sandwich?"
    Then voting should have 2 options
    And voting should have option "yes"
    And voting should have option "no"

  Scenario: Custom voting options
    When I create new voting "What is your favorite Devoxx food?"
    And voting options are "Breakfast:Salad:Soup:Sandwich"
    Then voting title should be "What is your favorite Devoxx food?"
    And voting should have options
      | Breakfast |
      | Salad     |
      | Soup      |
      | Sandwich  |