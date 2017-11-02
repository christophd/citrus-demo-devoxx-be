Feature: Create voting

  As a user I want to create new votings. Each voting is given default vote  options.
  The user should be able to set custom vote options.

  Scenario: Default voting options
    When I create new voting
    Then voting title should be "Do you like Devoxx?"
    And voting should have options
      | yes |
      | no  |

  Scenario: Custom voting options
    When I create new voting "What type of Devoxx ticket do you have?"
    And voting options are "University:Conference:Combi"
    Then voting title should be "What type of Devoxx ticket do you have?"
    And voting should have options
      | University |
      | Conference |
      | Combi      |