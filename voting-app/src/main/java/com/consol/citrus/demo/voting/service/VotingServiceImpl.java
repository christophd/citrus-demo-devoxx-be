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

package com.consol.citrus.demo.voting.service;

import com.consol.citrus.demo.voting.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Christoph Deppisch
 */
@Service
public class VotingServiceImpl implements VotingService {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(VotingServiceImpl.class);

    /** In memory storage of votings */
    private Map<String, Voting> votings = new HashMap<>();

    @Autowired
    private List<ReportingService> reportingServices;

    @Override
    public List<Voting> getVotings() {
        return Arrays.asList(votings.values().toArray(new Voting[votings.size()]));
    }

    @Override
    public void add(Voting voting) {
        votings.put(voting.getId(), voting);
    }

    @Override
    public void vote(Vote vote) {
        checkVoting(vote.getVotingId());

        Voting voting = votings.get(vote.getVotingId());
        if (voting.isClosed()) {
            throw new RuntimeException("Failed to add vote - voting is closed!");
        }

        for (VoteOption voteOption : voting.getOptions()) {
            if (voteOption.getName().equals(vote.getOption())) {
                voteOption.increment();
            }
        }
    }

    @Override
    public Voting get(String votingId) {
        checkVoting(votingId);
        return votings.get(votingId);
    }

    @Override
    public void remove(String votingId) {
        checkVoting(votingId);
        votings.remove(votingId);
    }

    @Override
    public VoteOption getTopVote(Voting voting) {
        VoteOption topVote = null;

        for (VoteOption voteOption : voting.getOptions()) {
            if (topVote == null ||
                    voteOption.getVotes() > topVote.getVotes()) {
                topVote = voteOption;
            }
        }

        return topVote;
    }

    @Override
    public void close(Voting voting) {
        voting.setClosed(true);

        log.info("Close voting: " + voting.getId());

        if (voting.isReport()) {
            for (ReportingService reportingService : reportingServices) {
                reportingService.report(voting, getTopVote(voting));
            }
        }
    }

    @Override
    public void clear() {
        votings.clear();
    }

    /**
     * Checks that voting id is known to the system.
     * @param votingId
     * @throws RuntimeException
     */
    private void checkVoting(String votingId) throws RuntimeException {
        if (!votings.containsKey(votingId)) {
            throw new RuntimeException("No such voting for id: " + votingId);
        }
    }
}
