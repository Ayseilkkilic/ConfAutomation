package com.confautomation.hooks;

import com.confautomation.support.AppiumDriverFactory;
import com.confautomation.support.DriverManager;
import io.appium.java_client.android.AndroidDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;

public class TestHooks {

    private static final ThreadLocal<AppiumDriverFactory> FACTORY = ThreadLocal.withInitial(AppiumDriverFactory::new);

    @Before(order = 0)
    public void beforeScenario() {
        // Ensure driver exists for this thread, created once per thread
        DriverManager.ensureDriver(FACTORY.get());
    }

    @Before(order = 1)
    public void relaunchAppBeforeEachScenario() {
        AndroidDriver driver = DriverManager.getDriver();
        if (driver == null) return;
        final String appId = "aero.tci.entertainment";
        try { driver.terminateApp(appId); } catch (Exception ignored) {}
        try { driver.activateApp(appId); } catch (Exception ignored) {}
    }

    @After(order = 1000)
    public void attachArtifacts(Scenario scenario) {
        AndroidDriver driver = DriverManager.getDriver();
        if (driver == null || !scenario.isFailed()) {
            return;
        }

        try {
            byte[] screenshot = driver.getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Failure screenshot", "image/png",
                    new ByteArrayInputStream(screenshot), ".png");
        } catch (Exception ignored) {}

        try {
            String source = driver.getPageSource();
            Allure.addAttachment("Page source", "application/xml", source, ".xml");
        } catch (Exception ignored) {}
    }

    // Teardown merkezini runner'ın thread-özel finally bloğunda tuttuk.
    // Buradaki @AfterAll kapatmayı kaldırıyoruz; böylece çift kapatma olmaz.
}
