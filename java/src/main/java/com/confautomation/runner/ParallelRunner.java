package com.confautomation.runner;

import com.confautomation.support.DriverManager;
import com.confautomation.support.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.cucumber.core.cli.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ParallelRunner {
    public static void main(String[] args) throws Exception {
        Map<String, String> deviceList = JsonUtils.readJsonFileRelative("deviceList.json", new TypeReference<Map<String, String>>(){});
        List<String> connected = AdbUtils.connectedDevices();

        // Filter devices
        Map<String, String> filtered = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : deviceList.entrySet()) {
            String name = e.getKey();
            String udid = e.getValue();
            if (connected.contains(udid)) {
                filtered.put(name, udid);
            } else if (udid.contains(":") && udid.replace(".", "").replace(":", "").chars().allMatch(Character::isDigit)) {
                boolean ok = AdbUtils.connect(udid);
                if (ok) {
                    filtered.put(name, udid);
                } else {
                    System.out.println("⚠️ " + name + " bağlanamadı, test kapsamı dışına alınıyor.");
                }
            }
        }

        System.out.println("Birden fazla tag ve cihaz girebilirsiniz. Örnek: Ekran1 için @login,@search gibi virgül ile ayırarak yazın.");
        Map<String, List<String>> platformTags = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("Platform ve tag(ler)ini girin (örnek: ekran1=@login,@search), bitirmek için boş bırakın: ");
                String line = br.readLine();
                if (line == null || line.trim().isEmpty()) break;
                // Support shorthand: if user types only tags like "@smoke",
                // treat it as allDevice for convenience
                if (!line.contains("=") && line.trim().startsWith("@")) {
                    String[] tags = line.trim().split(",");
                    List<String> list = new ArrayList<>();
                    for (String t : tags) {
                        t = t.trim();
                        if (!t.startsWith("@")) t = "@" + t;
                        list.add(t);
                    }
                    platformTags.put("allDevice", list);
                } else if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    String platform = parts[0].trim();
                    String[] tags = parts[1].split(",");
                    List<String> list = new ArrayList<>();
                    for (String t : tags) {
                        t = t.trim();
                        if (!t.startsWith("@")) t = "@" + t;
                        list.add(t);
                    }
                    platformTags.put(platform, list);
                }
            }
        }

        if (platformTags.isEmpty()) {
            System.out.println("Platform ve tag bilgisi girilmedi. Çıkılıyor.");
            return;
        }

        AppiumServerManager serverManager = new AppiumServerManager();
        Map<String, Integer> devicePorts = new LinkedHashMap<>();
        List<Process> appiumProcs = new ArrayList<>();

        for (String platform : platformTags.keySet()) {
            if (platform.equals("allDevice")) {
                int i = 0;
                for (String deviceName : filtered.keySet()) {
                    int port = 4730 + i++;
                    // Try connect if ip:port-like and not already connected
                    String deviceIp = filtered.get(deviceName);
                    if (!connected.contains(deviceIp) && deviceIp.contains(":") && deviceIp.replace(".", "").replace(":", "").chars().allMatch(Character::isDigit)) {
                        AdbUtils.connect(deviceIp);
                    }
                    Process p = serverManager.startAppium(port);
                    if (p == null) throw new RuntimeException("❌ " + deviceName + " için Appium başlatılamadı!");
                    appiumProcs.add(p);
                    devicePorts.put(deviceName, port);
                }
            } else if (filtered.containsKey(platform)) {
                int port = 4723;
                String deviceId = filtered.get(platform);
                if (!connected.contains(deviceId) && deviceId.contains(":") && deviceId.replace(".", "").replace(":", "").chars().allMatch(Character::isDigit)) {
                    AdbUtils.connect(deviceId);
                }
                Process p = serverManager.startAppium(port);
                if (p == null) throw new RuntimeException("❌ " + platform + " için Appium başlatılamadı!");
                appiumProcs.add(p);
                devicePorts.put(platform, port);
            }
        }

        // Persist device_ports.json
        JsonUtils.writeJsonFileRelative("device_ports.json", new LinkedHashMap<>(devicePorts));

        // Run Cucumber in threads
        List<Thread> threads = new CopyOnWriteArrayList<>();
        for (Map.Entry<String, List<String>> entry : platformTags.entrySet()) {
            String platform = entry.getKey();
            List<String> tags = entry.getValue();
            if (platform.equals("allDevice")) {
                for (String deviceName : filtered.keySet()) {
                    for (String tag : tags) {
                        Thread t = new Thread(() -> runCucumberFor(tag, deviceName));
                        threads.add(t);
                        t.start();
                    }
                }
            } else {
                for (String tag : tags) {
                    Thread t = new Thread(() -> runCucumberFor(tag, platform));
                    threads.add(t);
                    t.start();
                }
            }
        }

        for (Thread t : threads) t.join();

        // stop appium
        serverManager.shutdownAll();
    }

    private static void runCucumberFor(String tag, String platformName) {
        System.out.println("🔎 " + platformName.toUpperCase() + " için Cucumber Komutu: --tags=" + tag);
        // Set platform for this thread
        DriverManager.setPlatform(platformName);
        // Invoke Cucumber programmatically
        List<String> argv = new ArrayList<>();
        argv.add("--plugin"); argv.add("pretty");
        argv.add("--plugin"); argv.add("summary");
        argv.add("--glue"); argv.add("com.confautomation.steps");
        argv.add("--glue"); argv.add("com.confautomation.hooks");
        argv.add("--tags"); argv.add(tag);
        argv.add("classpath:features");

        try {
            byte exit = Main.run(argv.toArray(new String[0]), Thread.currentThread().getContextClassLoader());
            if (exit != 0) {
                System.out.println("❌ " + platformName.toUpperCase() + " için hata: exit=" + exit);
            }
        } catch (Exception e) {
            System.out.println("❌ " + platformName.toUpperCase() + " için hata: " + e);
        } finally {
            // Close driver for this thread
            try { DriverManager.quitAllForCurrentThread(); } catch (Exception ignored) {}
        }
    }
}
