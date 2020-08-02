package com.fut.desktop.app.steps;

import com.fut.desktop.app.page.NavigationPage;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NavigationStep {

    @Autowired
    private final NavigationPage navigationPage;

    public NavigationStep(NavigationPage navigationPage) {
        this.navigationPage = navigationPage;
    }

    @Then("I can see the $sectionName section")
    public void iCanSeeTheSection(final String sectionName) {
        Assert.assertTrue(navigationPage.navigateToSection(sectionName).getTagName().contains(sectionName));
    }

    @Given("I navigate to the $sectionName section")
    public void iNavigateToTheSection(final String sectionName) {
        navigationPage.clickSidebarButton(sectionName);
    }

    @Then("I take a screenshot")
    public void takeAScreenshot(){
        navigationPage.takeScreenshot();
    }

    @Then("I verify i am in the $sectionName section")
    public void verifyInTradePile(String sectionName) {
        Assert.assertNotNull(navigationPage.verifySection(sectionName));
    }
}
