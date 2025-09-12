package com.confautomation.hooks;

import com.confautomation.support.AppiumDriverFactory;
import com.confautomation.support.DriverManager;
import io.cucumber.java.Before;
import io.appium.java_client.android.AndroidDriver;

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

    // Teardown merkezini runner'ın thread-özel finally bloğunda tuttuk.
    // Buradaki @AfterAll kapatmayı kaldırıyoruz; böylece çift kapatma olmaz.
}
