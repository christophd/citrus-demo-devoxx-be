Feature: Voting Http REST API

  Background:
    Given URL: http://localhost:8080/rest/services
    Given variables
      | id      | citrus:randomUUID()  |
      | title   | Do you like Devoxx? |
      | options | [ { "name": "yes", "votes": 0 }, { "name": "no", "votes": 0 } ] |
      | report  | true                 |

  Scenario: Clear voting list
    When send DELETE /voting
    Then receive status 200 OK

  Scenario: Get empty voting list
    Given Accept: application/json
    When send GET /voting
    Then Payload: []
    And receive status 200 OK

  Scenario: Create voting
    Given Payload:
    """
    {
      "id": "${id}",
      "title": "${title}",
      "options": ${options},
      "report": ${report}
    }
    """
    And Content-Type: application/json
    When send POST /voting
    Then receive status 200 OK

  Scenario: Get voting list
    When send GET /voting
    Then validate $.size() is 1
    Then validate $..title is Do you like Devoxx?
    Then validate $..report is true
    And receive status 200 OK