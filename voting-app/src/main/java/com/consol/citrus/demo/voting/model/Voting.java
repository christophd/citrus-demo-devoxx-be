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

import java.util.*;

/**
 * @author Christoph Deppisch
 */
public class Voting {

    private String id;
    private String title;

    private boolean closed;
    private boolean report;

    private List<VoteOption> options = new ArrayList<>();

    /**
     * Default constructor.
     */
    public Voting() {
        this(UUID.randomUUID(), "Do you like Devoxx?", "yes", "no");
    }

    /**
     * Constructor using title.
     * @param title
     */
    public Voting(String title) {
        this(UUID.randomUUID(), title, "yes", "no");
    }

    /**
     * Constructor using title and options.
     * @param title
     * @param options
     */
    public Voting(String title, String ... options) {
        this(UUID.randomUUID(), title, options);
    }

    /**
     * Constructor using id, title and options.
     * @param id
     * @param title
     * @param options
     */
    public Voting(UUID id, String title, String ... options) {
        this.id = id.toString();
        this.title = title;

        if (options.length == 0) {
            this.options.add(new VoteOption("yes"));
            this.options.add(new VoteOption("no"));
        }

        for (String option : options) {
            this.options.add(new VoteOption(option.trim()));
        }
    }

    /**
     * Gets the voting option by its name.
     * @param name
     * @return
     */
    public VoteOption getOption(String name) {
        for (VoteOption option : options) {
            if (option.getName().equals(name)) {
                return option;
            }
        }

        throw new RuntimeException("No such voting option: " + name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isReport() {
        return report;
    }

    public void setReport(boolean report) {
        this.report = report;
    }

    public List<VoteOption> getOptions() {
        return options;
    }

    public void setOptions(List<VoteOption> options) {
        this.options = options;
    }
}
