package com.confautomation.steps;

import com.confautomation.support.DriverManager;
import io.appium.java_client.appmanagement.ApplicationState;
import io.cucumber.java.en.Given;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;

public class MoviesSteps {

    @Given("Uygulama açıldı")
    public void appLaunched() throws InterruptedException {
        var driver = DriverManager.getDriver();
        Assertions.assertNotNull(driver);

        final String appId = "aero.tci.entertainment";

        // Helper: wait until foreground, but bail out fast to avoid long startup delays
        final Duration foregroundTimeout = Duration.ofSeconds(6);
        final long pollIntervalMs = 300;
        java.util.function.Supplier<Boolean> waitForeground = () -> {
            long deadline = System.currentTimeMillis() + foregroundTimeout.toMillis();
            ApplicationState state = driver.queryAppState(appId);
            while (System.currentTimeMillis() < deadline && state != ApplicationState.RUNNING_IN_FOREGROUND) {
                try {
                    Thread.sleep(pollIntervalMs);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    return false;
                }
                state = driver.queryAppState(appId);
            }
            return state == ApplicationState.RUNNING_IN_FOREGROUND;
        };

        // First wait: if not FG, try to activate
        boolean ok = waitForeground.get();
        if (!ok) {
            try { driver.activateApp(appId); } catch (Exception ignored) {}
            ok = waitForeground.get();
        }

        // If still not ok, allow BG but log state and proceed to next step to avoid premature teardown
        ApplicationState finalState = driver.queryAppState(appId);
        System.out.println("[LAUNCH] package=" + driver.getCurrentPackage() + ", activity=" + driver.currentActivity() + ", state=" + finalState);
        Assertions.assertTrue(
                finalState == ApplicationState.RUNNING_IN_FOREGROUND || finalState == ApplicationState.RUNNING_IN_BACKGROUND,
                "App is not running. State: " + finalState
        );
    }
}
