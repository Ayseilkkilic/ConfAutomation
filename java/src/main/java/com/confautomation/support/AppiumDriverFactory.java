package com.confautomation.support;

import com.fasterxml.jackson.core.type.TypeReference;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.util.Map;

public class AppiumDriverFactory {
    private Map<String, String> deviceList; // name -> udid
    private Map<String, Integer> portMap;   // name -> port

    public AppiumDriverFactory() {
        try {
            deviceList = JsonUtils.readJsonFileRelative("deviceList.json", new TypeReference<Map<String, String>>(){});
            portMap = JsonUtils.readJsonFileRelative("device_ports.json", new TypeReference<Map<String, Integer>>(){});
        } catch (Exception e) {
            throw new RuntimeException("Failed to read device or port mapping: " + e.getMessage(), e);
        }
    }

    public AndroidDriver createDriverForPlatform(String platformName) {
        String udid = deviceList.get(platformName);
        if (udid == null) {
            throw new IllegalArgumentException("Unknown platform: " + platformName);
        }

        int port = portMap.getOrDefault(platformName, 4723);
        String serverUrl = String.format("http://localhost:%d", port);

        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setAutomationName("UiAutomator2")
                .setUdid(udid)
                .amend("appPackage", "aero.tci.entertainment")
                .amend("appActivity", "aero.tci.entertainment.activity.SplashActivity")
                // Fresh launch every run
                .amend("noReset", true)
                .amend("fullReset", false)
                .amend("dontStopAppOnReset", true)
                .amend("autoGrantPermissions", true)
                .amend("newCommandTimeout", 180)
                .amend("appWaitPackage", "aero.tci.entertainment")
                .amend("appWaitActivity", "*")
                .amend("appWaitDuration", 20000);
        try {
            AndroidDriver driver = new AndroidDriver(URI.create(serverUrl).toURL(), options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
            return driver;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
