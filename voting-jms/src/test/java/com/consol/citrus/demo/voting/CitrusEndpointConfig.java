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

package com.consol.citrus.demo.voting;

import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.dsl.runner.TestRunnerBeforeTestSupport;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import com.consol.citrus.mail.server.MailServer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.ConnectionFactory;

/**
 * @author Christoph Deppisch
 */
@Configuration
public class CitrusEndpointConfig {

    @Bean
    public HttpClient votingClient() {
        return CitrusEndpoints.http()
                .client()
                .requestUrl("http://localhost:8080/rest/services")
                .build();
    }

    @Bean
    public JmsEndpoint createVotingEndpoint() {
        return CitrusEndpoints.jms()
                .asynchronous()
                .connectionFactory(connectionFactory())
                .destination("jms.voting.create")
                .build();
    }

    @Bean
    public JmsEndpoint voteEndpoint() {
        return CitrusEndpoints.jms()
                .asynchronous()
                .connectionFactory(connectionFactory())
                .destination("jms.voting.inbound")
                .build();
    }

    @Bean
    public JmsEndpoint reportingEndpoint() {
        return CitrusEndpoints.jms()
                .asynchronous()
                .connectionFactory(connectionFactory())
                .destination("jms.voting.report")
                .build();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL("tcp://localhost:61616");
        return activeMQConnectionFactory;
    }

    @Bean
    public MailServer mailServer() {
        return CitrusEndpoints.mail()
                .server()
                .port(2222)
                .autoStart(true)
                .autoAccept(true)
                .build();
    }

    @Bean
    public TestRunnerBeforeTestSupport beforeTest() {
        return new TestRunnerBeforeTestSupport() {
            @Override
            public void beforeTest(TestRunner runner) {
                runner.purgeQueues(action -> action
                        .connectionFactory(connectionFactory())
                        .queue("jms.voting.create")
                        .queue("jms.voting.report")
                        .queue("jms.voting.inbound"));
            }
        };
    }
}
