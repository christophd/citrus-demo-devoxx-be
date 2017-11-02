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

package com.consol.citrus.demo.voting.model;

/**
 * @author Christoph Deppisch
 */
public class Vote {

    private String votingId;
    private String option;

    /**
     * Default constructor.
     */
    public Vote() {
        super();
    }

    /**
     * Constructor using fields.
     * @param votingId
     * @param option
     */
    public Vote(String votingId, String option) {
        this.votingId = votingId;
        this.option = option;
    }

    /**
     * Gets the votingId.
     *
     * @return
     */
    public String getVotingId() {
        return votingId;
    }

    /**
     * Sets the votingId.
     *
     * @param votingId
     */
    public void setVotingId(String votingId) {
        this.votingId = votingId;
    }

    /**
     * Gets the option.
     *
     * @return
     */
    public String getOption() {
        return option;
    }

    /**
     * Sets the option.
     *
     * @param option
     */
    public void setOption(String option) {
        this.option = option;
    }
}
