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

import com.consol.citrus.container.SequenceAfterSuite;
import com.consol.citrus.container.SequenceAfterTest;
import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.dsl.runner.*;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.selenium.endpoint.SeleniumBrowser;
import org.openqa.selenium.remote.BrowserType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public SeleniumBrowser browser() {
        return CitrusEndpoints.selenium()
                .browser()
                .type(BrowserType.CHROME)
                .build();
    }

    @Bean
    public SequenceAfterSuite afterSuite() {
        return new TestRunnerAfterSuiteSupport() {
            @Override
            public void afterSuite(TestRunner runner) {
                runner.selenium(builder -> builder.browser(browser()).stop());
            }
        };
    }

    @Bean
    public SequenceAfterTest afterTest() {
        return new TestRunnerAfterTestSupport() {
            @Override
            public void afterTest(TestRunner runner) {
                runner.sleep(500);
            }
        };
    }
}
