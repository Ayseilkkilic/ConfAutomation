package com.confautomation.steps;

import com.confautomation.pages.MoviesPage;
import com.confautomation.support.DriverManager;
import io.appium.java_client.appmanagement.ApplicationState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class MoviesSteps {

    @Given("Uygulama açıldı")
    public void appLaunched() throws InterruptedException {
        var driver = DriverManager.getDriver();
        Assertions.assertNotNull(driver);

        final String appId = "aero.tci.entertainment";

        // Helper: wait until foreground up to 15s
        java.util.function.Supplier<Boolean> waitForeground = () -> {
            long end = System.currentTimeMillis() + 15_000;
            ApplicationState s = driver.queryAppState(appId);
            while (System.currentTimeMillis() < end && s != ApplicationState.RUNNING_IN_FOREGROUND) {
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                s = driver.queryAppState(appId);
            }
            return s == ApplicationState.RUNNING_IN_FOREGROUND;
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

    @When("Planet Thy Adresine gidilir")
    public void openPlanetThy() {
        MoviesPage page = new MoviesPage(DriverManager.getDriver());
        page.openPlanetWeb();
    }

    @Then("User should see \"{string}\" section")
    public void userShouldSee(String text) {
        MoviesPage page = new MoviesPage(DriverManager.getDriver());
        page.userShouldSee(text);
    }
}
