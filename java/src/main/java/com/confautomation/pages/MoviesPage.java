package com.confautomation.pages;

import com.confautomation.common.Base;
import com.confautomation.locators.CommonLocators;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class MoviesPage {
    private final AndroidDriver driver;
    private final Base base;

    public MoviesPage(AndroidDriver driver) {
        this.driver = driver;
        this.base = new Base(driver);
    }

    public void openPlanetWeb() {
        driver.get("https://www.google.com");
    }

    public void userShouldSee(String text) {
        String xpath = CommonLocators.TEXT_XPATH.replace("{}", text);
        base.getElement(AppiumBy.xpath(xpath), xpath);
    }
}

