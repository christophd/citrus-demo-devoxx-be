Feature: Voting Http REST API

  Background:
    Given variables
      | baseUrl | http://localhost:8080/rest/services  |
      | id      | citrus:randomUUID()  |
      | title   | Do you like Devoxx? |
      | options | [ { "name": "yes", "votes": 0 }, { "name": "no", "votes": 0 } ] |
      | report  | true                 |

  Scenario: Clear voting list
    When send request
"""
DELETE ${baseUrl}/voting
Accept-Charset:utf-8
Accept:application/json, application/*+json, */*
Host:localhost:8080
Content-Type:text/plain;charset=UTF-8

"""
    Then receive response
"""
HTTP/1.1 200
X-Application-Context:@contains('application')@
Transfer-Encoding:chunked
Date: @ignore@

"""

  Scenario: Get empty voting list
    When send request
"""
GET ${baseUrl}/voting
Accept-Charset:utf-8
Accept:application/json, application/*+json, */*
Host:localhost:8080
Content-Type:text/plain;charset=UTF-8

"""
    Then receive response
"""
HTTP/1.1 200 OK
Content-Type:@equalsIgnoreCase('application/json;charset=UTF-8')@
X-Application-Context:@contains('application')@
Transfer-Encoding:chunked
Date: @ignore@

[]
"""

  Scenario: Create voting
    When send request
"""
POST ${baseUrl}/voting
Accept-Charset:utf-8
Accept:application/json, application/*+json, */*
Host:localhost:8080
Content-Type:application/json;charset=UTF-8

{
  "id": "${id}",
  "title": "${title}",
  "options": ${options},
  "report": ${report}
}
"""
    Then receive response
"""
HTTP/1.1 200 OK
Content-Type:@equalsIgnoreCase('application/json;charset=UTF-8')@
X-Application-Context:@contains('application')@
Transfer-Encoding:chunked
Date: @ignore@

{
  "id": "${id}",
  "title": "${title}",
  "options": ${options},
  "report": ${report},
  "closed": false
}
"""

  Scenario: Get voting list
    When send request
"""
GET ${baseUrl}/voting
Accept-Charset:utf-8
Accept:application/json, application/*+json, */*
Host:localhost:8080
Content-Type:text/plain;charset=UTF-8

"""
    Then receive response
"""
HTTP/1.1 200 OK
Content-Type:@equalsIgnoreCase('application/json;charset=UTF-8')@
X-Application-Context:@contains('application')@
Transfer-Encoding:chunked
Date: @ignore@

[
  {
    "id": "@ignore@",
    "title": "${title}",
    "options": ${options},
    "report": ${report},
    "closed": false
  }
]
"""