package com.confautomation.common;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Arrays;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

public class Base {
    private final AndroidDriver driver;

    public Base(AndroidDriver driver) {
        this.driver = driver;
    }

    public WebElement getElement(By by, String selector, Integer... waitSec) {
        int timeout = (waitSec != null && waitSec.length >= 1) ? waitSec[0] : 30;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator(by, selector)));
        return driver.findElement(locator(by, selector));
    }

    public List<WebElement> getElements(By by, String selector) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator(by, selector)));
        return driver.findElements(locator(by, selector));
    }

    public void clickElement(By by, String selector) {
        getElement(by, selector).click();
    }

    public void clickElementWithWait(By by, String selector, int wait) {
        getElement(by, selector, wait).click();
    }

    public void enterText(By by, String selector, String text) {
        WebElement element = driver.findElement(locator(by, selector));
        element.clear();
        element.sendKeys(text);
    }

    public void sendKeys(By by, String selector, String text) {
        WebElement element = driver.findElement(locator(by, selector));
        element.sendKeys(text);
    }

    public void clickEnter() {
        driver.pressKey(new io.appium.java_client.android.nativekey.KeyEvent(io.appium.java_client.android.nativekey.AndroidKey.ENTER));
    }

    public void closeKeyboard() {
        try {
            if (driver.isKeyboardShown()) {
                driver.hideKeyboard();
            }
        } catch (Exception ignored) {}
    }

    public String getText(By by, String selector) {
        WebElement element = getElement(by, selector);
        return element.getText();
    }

    public boolean checkElementVisibility(By by, String selector) {
        try {
            return getElement(by, selector).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void waitElementVisibility(int seconds, By by, String selector) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator(by, selector)));
    }

    public void scrollUntilVisibleText(String text) {
        driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"" + text + "\").instance(0))"));
    }

    public void scrollDown() {
        var size = driver.manage().window().getSize();
        int x = (int) (size.width * 0.7);
        int yStart = (int) (size.height * 0.65);
        int yEnd = (int) (size.height * 0.32);
        swipe(x, yStart, x, yEnd, 800);
    }

    private void swipe(int startX, int startY, int endX, int endY, int durationMs) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(durationMs), PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Arrays.asList(swipe));
    }

    public void tap(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(tap));
    }

    public void tapCenter(WebElement element) {
        Rectangle rect = element.getRect();
        int cx = rect.getX() + rect.getWidth() / 2;
        int cy = rect.getY() + rect.getHeight() / 2;
        tap(cx, cy);
    }

    public WebElement findElementWithScroll(By by, String selector, int maxScrolls) {
        By finalBy = locator(by, selector);
        for (int i = 0; i <= Math.max(0, maxScrolls); i++) {
            List<WebElement> candidates = driver.findElements(finalBy);
            for (WebElement candidate : candidates) {
                if (candidate.isDisplayed()) {
                    return candidate;
                }
            }
            scrollDown();
        }
        throw new NoSuchElementException("Element not found after scrolling: " + selector);
    }

    private By locator(By by, String selector) {
        // Accept a By and selector to keep call sites consistent; rebuild the final By here.
        // For simplicity, map by.toString() and default to XPath.
        String b = by.toString();
        if (b.contains("By.xpath") || b.contains("xpath")) {
            return By.xpath(selector);
        } else if (b.contains("By.id") || b.contains("id")) {
            return By.id(selector);
        } else if (b.contains("By.name") || b.contains("name")) {
            return By.name(selector);
        }
        // default to XPath
        return By.xpath(selector);
    }
}
