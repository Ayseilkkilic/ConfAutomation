package com.confautomation.steps;

import com.confautomation.common.Base;
import com.confautomation.locators.CommonLocators;
import com.confautomation.support.DriverManager;
import io.appium.java_client.AppiumBy;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebElement;

public class CommonSteps {

    @Given("User click on \"{string}\" button")
    public void clickOnButtonWithText(String text) {
        var driver = DriverManager.getDriver();
        var base = new Base(driver);
        String xpath = CommonLocators.TEXT_XPATH.replace("{}", text);
        base.clickElement(AppiumBy.xpath(xpath), xpath);
    }

    @Then("\"{string}\" section should be displayed")
    public void sectionShouldBeDisplayed(String text) {
        var driver = DriverManager.getDriver();
        var base = new Base(driver);
        String xpath = CommonLocators.TEXT_XPATH.replace("{}", text);
        base.getElement(AppiumBy.xpath(xpath), xpath);
    }

    @Then("\"{string}\" should be visible on the screen")
    public void textShouldBeVisibleOnScreen(String text) {
        var driver = DriverManager.getDriver();
        var base = new Base(driver);
        String xpath = CommonLocators.TEXT_XPATH.replace("{}", text);
        WebElement element = base.getElement(AppiumBy.xpath(xpath), xpath);
        Assertions.assertTrue(element.isDisplayed(), '"' + text + '"' + " is not visible on the screen.");
    }

    @Then("{int} saniye beklenir")
    public void waitSeconds(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000L);
    }
}

