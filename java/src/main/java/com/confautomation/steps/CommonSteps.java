package com.confautomation.steps;

import com.confautomation.common.Base;
import com.confautomation.locators.CommonLocators;
import com.confautomation.support.DriverManager;
import io.appium.java_client.AppiumBy;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import java.util.Arrays;

public class CommonSteps {

    @Then("User click on {string} button")
    public void clickOnButtonWithText(String text) {
        var driver = DriverManager.getDriver();
        var base = new Base(driver);
        String exact = CommonLocators.TEXT_XPATH.replace("{}", text);
        String normalized = "//*[normalize-space(@text)='" + text + "' or normalize-space(.)='" + text + "']";
        String contains = "//*[contains(@text,'" + text + "') or contains(normalize-space(.),'" + text + "')]";

        String withinWheel = "//*[@resource-id='aero.tci.entertainment:id/wheelPicker']//*[normalize-space(@text)='" + text + "' or contains(@text,'" + text + "') or contains(normalize-space(.),'" + text + "')]";
        String[] candidates = new String[] { exact, normalized, contains, withinWheel };

        for (String xp : candidates) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
                WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(AppiumBy.xpath(xp)));
                try {
                    WebElement clickable = new WebDriverWait(driver, Duration.ofSeconds(4))
                            .until(ExpectedConditions.elementToBeClickable(AppiumBy.xpath(xp)));
                    clickable.click();
                    return;
                } catch (Exception ignored) {
                    // coordinate tap fallback
                    Rectangle r = el.getRect();
                    int cx = r.getX() + r.getWidth() / 2;
                    int cy = r.getY() + r.getHeight() / 2;
                    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
                    Sequence tap = new Sequence(finger, 1);
                    tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), cx, cy));
                    tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
                    tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
                    driver.perform(Arrays.asList(tap));
                    return;
                }
            } catch (Exception ignored) {}
        }

        // Last resort A: tap wheelPicker center (selects centered item)
        try {
            WebElement wheel = driver.findElement(AppiumBy.id("aero.tci.entertainment:id/wheelPicker"));
            Rectangle r = wheel.getRect();
            int cx = r.getX() + r.getWidth() / 2;
            int cy = r.getY() + r.getHeight() / 2;
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence tap = new Sequence(finger, 1);
            tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), cx, cy));
            tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(Arrays.asList(tap));
            return;
        } catch (Exception ignored) {}

        // Last resort B: clickable ancestor of the best-guess locator (exact)
        try {
            String clickableAncestor = "(" + exact + ")/ancestor-or-self::*[@clickable='true'][1]";
            WebElement ancestor = driver.findElement(AppiumBy.xpath(clickableAncestor));
            ancestor.click();
            return;
        } catch (Exception ignored) {}

        Assertions.fail("Could not click element with text: " + text);
    }

    @Then("{string} should be visible on the screen")
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
