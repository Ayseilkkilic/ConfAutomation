package com.confautomation.support;

import io.appium.java_client.android.AndroidDriver;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DriverManager {
    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<String> PLATFORM = new ThreadLocal<>();

    // Keep references to close later if needed
    private static final Map<Long, AndroidDriver> THREAD_DRIVERS = new ConcurrentHashMap<>();

    public static void setPlatform(String platformName) {
        PLATFORM.set(platformName);
    }

    public static String getPlatform() {
        return PLATFORM.get();
    }

    public static void setDriver(AndroidDriver driver) {
        DRIVER.set(driver);
        if (driver != null) {
            THREAD_DRIVERS.put(Thread.currentThread().getId(), driver);
        }
    }

    public static AndroidDriver getDriver() {
        return DRIVER.get();
    }

    public static void ensureDriver(AppiumDriverFactory factory) {
        if (getDriver() == null) {
            String platform = Optional.ofNullable(getPlatform()).orElseThrow(() -> new IllegalStateException("Platform not set for thread"));
            setDriver(factory.createDriverForPlatform(platform));
        }
    }

    public static void quitDriver() {
        AndroidDriver d = DRIVER.get();
        if (d != null) {
            try { d.quit(); } catch (Exception ignored) {}
        }
        DRIVER.remove();
        THREAD_DRIVERS.remove(Thread.currentThread().getId());
        PLATFORM.remove();
    }

    public static void quitAllForCurrentThread() {
        quitDriver();
    }
}

