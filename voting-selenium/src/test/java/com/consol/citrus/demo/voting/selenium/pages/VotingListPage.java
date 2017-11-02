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

package com.consol.citrus.demo.voting.selenium.pages;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.selenium.endpoint.SeleniumBrowser;
import com.consol.citrus.selenium.model.PageValidator;
import com.consol.citrus.selenium.model.WebPage;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.util.StringUtils;

/**
 * @author Christoph Deppisch
 */
public class VotingListPage implements WebPage, PageValidator<VotingListPage> {

    @FindBy(tagName = "h1")
    private WebElement heading;

    @FindBy(id = "new-voting")
    private WebElement newVotingForm;

    /**
     * Submits new voting.
     * @param title
     * @param options
     */
    public void submit(String title, String options) {
        newVotingForm.findElement(By.id("title")).sendKeys(title);
        if (StringUtils.hasText(options)) {
            newVotingForm.findElement(By.id("options")).sendKeys(options.replaceAll(":", "\n"));
        }

        newVotingForm.submit();
    }

    @Override
    public void validate(VotingListPage webPage, SeleniumBrowser browser, TestContext context) {
        Assert.assertEquals("Voting list", heading.getText());
    }
}
