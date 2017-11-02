/*
 * Copyright 2006-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.demo.voting.jms;

import com.consol.citrus.annotations.CitrusEndpoint;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import cucumber.api.DataTable;
import cucumber.api.java.en.*;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * @author Christoph Deppisch
 */
public class VotingJmsSteps {

    @CitrusEndpoint
    private HttpClient votingClient;

    @CitrusEndpoint(name = "createVotingEndpoint")
    private JmsEndpoint createVotingEndpoint;

    @CitrusEndpoint(name = "voteEndpoint")
    private JmsEndpoint voteEndpoint;

    @CitrusEndpoint(name = "reportingEndpoint")
    private JmsEndpoint reportingEndpoint;

    @CitrusResource
    private TestRunner runner;

    @Given("^Voting list is empty$")
    public void clear() {
        runner.http(action -> action.client(votingClient)
                .send()
                .delete("/voting"));

        runner.http(action -> action.client(votingClient)
                .receive()
                .response(HttpStatus.OK));
    }

    @Given("^New voting \"([^\"]*)\"$")
    public void newVoting(String title) {
        runner.variable("id", "citrus:randomUUID()");
        runner.variable("title", title);
        runner.variable("options", buildOptionsAsJsonArray("yes:no"));
        runner.variable("closed", false);
        runner.variable("report", false);
    }

    @Given("^(?:the )?voting options are \"([^\"]*)\"$")
    public void votingOptions(String options) {
        runner.variable("options", buildOptionsAsJsonArray(options));
    }

    @Given("^(?:the )?reporting is enabled$")
    public void reportingIsEnabled() {
        runner.variable("report", true);
    }

    @When("^(?:I|client) creates? the voting$")
    public void createVoting() {
        runner.send(action -> action.endpoint(createVotingEndpoint)
            .header("_type", "com.consol.citrus.demo.voting.model.Voting")
            .payload("{ \"id\": \"${id}\", \"title\": \"${title}\", \"options\": ${options}, \"report\": ${report} }"));
    }

    @When("^(?:I|client) votes? for \"([^\"]*)\"$")
    public void voteFor(String option) {
        runner.send(action -> action.endpoint(voteEndpoint)
                .header("_type", "com.consol.citrus.demo.voting.model.Vote")
                .payload(String.format("{ \"votingId\": \"${id}\", \"option\": \"%s\" }", option)));
    }

    @When("^(?:I|client) votes? for \"([^\"]*)\" (\\d+) times$")
    public void voteForTimes(String option, int times) {
        for (int i = 1; i <= times; i++) {
            voteFor(option);
        }
    }

    @When("^(?:I|client) closes? the voting$")
    public void closeVoting() {
        runner.createVariable("closed", "true");

        runner.http(action -> action.client(votingClient)
            .send()
            .put("/voting/${id}/close"));

        runner.http(action -> action.client(votingClient)
            .receive()
            .response(HttpStatus.OK));

    }

    @Then("^(?:the )?reporting should receive vote results$")
    public void shouldReceiveReport(DataTable dataTable) {
        runner.createVariable("results", buildOptionsAsJsonArray(dataTable));

        runner.receive(action -> action.endpoint(reportingEndpoint)
                .payload("{ \"id\": \"${id}\", \"title\": \"${title}\", \"options\": ${results}, \"closed\": ${closed}, \"report\": ${report} }"));
    }

    @Then("^(?:the )?top vote should be \"([^\"]*)\"$")
    public void topVoteShouldBe(String option) {
        runner.http(action -> action.client(votingClient)
                .send()
                .get("/voting/${id}/top")
                .accept("application/json"));

        runner.http(action -> action.client(votingClient)
                .receive()
                .response(HttpStatus.OK)
                .payload("{ \"name\": \"" + option + "\", \"votes\": \"@ignore@\" }"));
    }

    /**
     * Builds proper Json array from data table containing option names and votes.
     * @param dataTable
     * @return
     */
    private String buildOptionsAsJsonArray(DataTable dataTable) {
        StringBuilder optionsExpression = new StringBuilder();
        Map<String, String> variables = dataTable.asMap(String.class, String.class);
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            optionsExpression.append(entry.getKey()).append("(").append(entry.getValue()).append("):");
        }

        return buildOptionsAsJsonArray(optionsExpression.toString().substring(0, optionsExpression.length() - 1));
    }

    /**
     * Builds proper Json array from options colon delimited list.
     * @param optionsExpression
     * @return
     */
    private String buildOptionsAsJsonArray(String optionsExpression) {
        String[] options = optionsExpression.split(":");
        StringBuilder optionsJson = new StringBuilder();

        optionsJson.append("[");
        for (String option : options) {
            String votes = "0";
            if (option.contains("(") && option.endsWith(")")) {
                votes = option.substring(option.indexOf("(") + 1, option.length() - 1);
                option = option.substring(0, option.indexOf("("));
            }

            optionsJson.append("{ \"name\": \"").append(option).append("\", \"votes\": ").append(votes).append(" }");
        }
        optionsJson.append("]");

        return optionsJson.toString().replaceAll("\\}\\{", "}, {");
    }
}
