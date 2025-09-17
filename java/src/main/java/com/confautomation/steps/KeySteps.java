package com.confautomation.steps;

import com.confautomation.common.Base;
import com.confautomation.locators.LocatorRepository;
import com.confautomation.support.DriverManager;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;

public class KeySteps {

    @When("click {string} element")
    public void clickElementByKey(String key) {
        var driver = DriverManager.getDriver();
        var base = new Base(driver);
        By by = LocatorRepository.getBy(key);
        String selector = LocatorRepository.getSelector(key);
        base.clickElement(by, selector);
    }

    @Then("text of {string} element contains {string}")
    public void textOfElementContains(String key, String expected) {
        var driver = DriverManager.getDriver();
        var base = new Base(driver);
        By by = LocatorRepository.getBy(key);
        String selector = LocatorRepository.getSelector(key);
        String actual = base.getText(by, selector);
        Assertions.assertTrue(actual != null && actual.contains(expected),
                "Expected to contain '" + expected + "' but was '" + actual + "'");
    }
}

