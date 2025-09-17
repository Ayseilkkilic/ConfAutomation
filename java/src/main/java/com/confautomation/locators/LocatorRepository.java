package com.confautomation.locators;

import com.confautomation.support.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class LocatorRepository {

    public static class LocatorDef {
        public String key;
        public String androidValue;
        public String androidType;
        public String iosValue;
        public String iosType;
    }

    private static final Map<String, LocatorDef> CACHE = new HashMap<>();
    private static boolean loaded = false;

    private static void ensureLoaded() {
        if (loaded) return;

        List<Path> candidates = List.of(
                Path.of("locators"),
                Path.of("..", "locators"),
                Path.of("..", "..", "locators")
        );

        boolean foundAny = false;
        for (Path dir : candidates) {
            if (Files.exists(dir) && Files.isDirectory(dir)) {
                try {
                    Files.list(dir)
                            .filter(p -> p.toString().endsWith(".json"))
                            .forEach(p -> loadFile(dir.relativize(p).toString()));
                    foundAny = true;
                } catch (IOException ignored) {}
            }
        }

        loaded = true;
        if (!foundAny) {
            System.out.println("[LocatorRepository] No locator JSON files found under locators/.");
        }
    }

    private static void loadFile(String relativeFile) {
        try {
            List<LocatorDef> defs = JsonUtils.readJsonFileRelative(
                    Path.of("locators", relativeFile).toString(),
                    new TypeReference<List<LocatorDef>>() {}
            );
            for (LocatorDef d : defs) {
                if (d != null && d.key != null) {
                    CACHE.put(d.key, d);
                }
            }
        } catch (Exception e) {
            System.out.println("[LocatorRepository] Failed to load locators from " + relativeFile + ": " + e.getMessage());
        }
    }

    public static By getBy(String key) {
        ensureLoaded();
        LocatorDef def = CACHE.get(key);
        if (def == null) throw new IllegalArgumentException("Locator key not found: " + key);
        String type = Optional.ofNullable(def.androidType).orElse("");
        String value = Optional.ofNullable(def.androidValue).orElse("");
        switch (type.toLowerCase()) {
            case "id":
                return By.id(value);
            case "xpath":
                return By.xpath(value);
            case "accessibilityid":
            case "accessibility":
            case "aid":
                return AppiumBy.accessibilityId(value);
            default:
                return By.xpath(value);
        }
    }

    public static String getSelector(String key) {
        ensureLoaded();
        LocatorDef def = CACHE.get(key);
        if (def == null) throw new IllegalArgumentException("Locator key not found: " + key);
        return Optional.ofNullable(def.androidValue).orElse("");
    }
}

