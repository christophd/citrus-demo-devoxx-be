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

package com.consol.citrus.demo.voting.web;

import com.consol.citrus.demo.voting.model.Vote;
import com.consol.citrus.demo.voting.model.Voting;
import com.consol.citrus.demo.voting.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("/voting")
public class VotingController {

    @Autowired
    private VotingService votingService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("votings", votingService.getVotings());
        return "voting";
    }

    @RequestMapping(method = RequestMethod.POST, headers = "content-type=application/x-www-form-urlencoded")
    public String addFormUrlencoded(@RequestParam(value = "title") String title,
                                    @RequestParam(value = "options") String optionsExpression) {
        String[] options;
        if (optionsExpression.contains(":")) {
            options = optionsExpression.split(":");
        } else if (optionsExpression.contains("\n")) {
            options = optionsExpression.split("\\n");
        } else {
            options = new String[] {optionsExpression};
        }

        votingService.add(new Voting(title, options));
        return "redirect:voting";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getVoting(@PathVariable("id") String votingId, Model model) {
        model.addAttribute("voting", votingService.get(votingId));
        return "voting-details";
    }

    @RequestMapping(value = "/{id}/vote", method = RequestMethod.GET)
    public String vote(@PathVariable("id") String votingId, @RequestParam("option") String option, Model model) {
        votingService.vote(new Vote(votingId, option));
        return getVoting(votingId, model);
    }

    @RequestMapping(value = "/{id}/close", method = RequestMethod.GET)
    public String close(@PathVariable("id") String votingId, Model model) {
        votingService.close(votingService.get(votingId));
        return getVoting(votingId, model);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String remove(@PathVariable("id") String votingId) {
        votingService.remove(votingId);
        return "redirect:voting";
    }
}
