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

import com.consol.citrus.demo.voting.model.VoteOption;
import com.consol.citrus.demo.voting.model.Voting;
import com.consol.citrus.demo.voting.service.ReportingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Christoph Deppisch
 */
@Service
@Profile("jms")
public class JmsReportingService implements ReportingService {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(JmsReportingService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public void report(Voting voting, VoteOption topVote) {
        log.info("Create reporting for voting: " + voting.getId());

        try {
            jmsTemplate.convertAndSend("jms.voting.report", voting);
        } catch (Exception e) {
            log.error("Failed to send JMS reporting", e);
        }
    }
}
