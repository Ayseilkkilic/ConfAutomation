package com.confautomation.runner;

import com.confautomation.support.DriverManager;
import com.confautomation.support.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.cucumber.core.cli.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ParallelRunner {
    public static void main(String[] args) throws Exception {
        Map<String, String> deviceList = JsonUtils.readJsonFileRelative("deviceList.json", new TypeReference<Map<String, String>>(){});
        List<String> connected = AdbUtils.connectedDevices();

        // Filter devices (only connected or connectable ip:port)
        Map<String, String> filtered = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : deviceList.entrySet()) {
            String name = e.getKey();
            String udid = e.getValue();
            if (connected.contains(udid)) {
                filtered.put(name, udid);
            } else if (looksLikeIpPort(udid)) {
                boolean ok = AdbUtils.connect(udid);
                if (ok) {
                    filtered.put(name, udid);
                } else {
                    System.out.println("⚠️ " + name + " (" + udid + ") bağlanamadı, test kapsamı dışına alınıyor.");
                }
            }
        }

        if (filtered.isEmpty()) {
            System.out.println("Uygun/bağlı cihaz bulunamadı. Lütfen cihazları kontrol edin.");
            return;
        }

        // Scan available tags from feature files
        List<String> discoveredTags = discoverTags();

        // Interactive selection: devices and tag expressions
        Map<String, String> deviceToTagExpr = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            // List devices
            System.out.println("\nBağlı cihazlar:");
            List<String> deviceNames = new ArrayList<>(filtered.keySet());
            for (int i = 0; i < deviceNames.size(); i++) {
                String dn = deviceNames.get(i);
                System.out.printf("  [%d] %s  (%s)%n", i + 1, dn, filtered.get(dn));
            }
            System.out.println("\nTag seçenekleri (feature'lardan keşfedildi):");
            if (discoveredTags.isEmpty()) {
                System.out.println("  (Etiket bulunamadı. Elle ifade girebilirsiniz, örn: @smoke and not @wip)");
            } else {
                for (int i = 0; i < discoveredTags.size(); i++) {
                    System.out.printf("  (%d) %s%n", i + 1, discoveredTags.get(i));
                }
            }

            System.out.println("\nAynı tag ifadesini birden fazla cihazda çalıştırmak için: all yazın");
            System.out.print("Cihaz seçimi (ör: 1 veya 1,3 veya all): ");
            String sel = safeReadLine(br);
            if (sel == null || sel.isBlank()) {
                System.out.println("Seçim yapılmadı, çıkılıyor.");
                return;
            }

            List<String> selectedDevices = new ArrayList<>();
            if (sel.trim().equalsIgnoreCase("all")) {
                selectedDevices.addAll(filtered.keySet());
            } else {
                for (String part : sel.split(",")) {
                    part = part.trim();
                    if (part.isEmpty()) continue;
                    try {
                        int idx = Integer.parseInt(part);
                        if (idx >= 1 && idx <= deviceNames.size()) {
                            selectedDevices.add(deviceNames.get(idx - 1));
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }

            if (selectedDevices.isEmpty()) {
                System.out.println("Geçerli cihaz seçimi yapılmadı. Çıkılıyor.");
                return;
            }

            if (selectedDevices.size() > 1) {
                System.out.print("Her cihaz için ayrı tag ifadesi girmek ister misiniz? [y/N]: ");
                String perDevice = safeReadLine(br);
                if (perDevice != null && (perDevice.equalsIgnoreCase("y") || perDevice.equalsIgnoreCase("e"))) {
                    for (String d : selectedDevices) {
                        System.out.println("\nCihaz: " + d);
                        String tagExpr = askTagExpression(br, discoveredTags);
                        deviceToTagExpr.put(d, tagExpr);
                    }
                } else {
                    String tagExpr = askTagExpression(br, discoveredTags);
                    for (String d : selectedDevices) deviceToTagExpr.put(d, tagExpr);
                }
            } else {
                // Single device
                for (String d : selectedDevices) {
                    System.out.println("\nCihaz: " + d);
                    String tagExpr = askTagExpression(br, discoveredTags);
                    deviceToTagExpr.put(d, tagExpr);
                }
            }
        }

        if (deviceToTagExpr.isEmpty()) {
            System.out.println("Tag ifadesi girilmedi. Çıkılıyor.");
            return;
        }

        AppiumServerManager serverManager = new AppiumServerManager();
        Map<String, Integer> devicePorts = new LinkedHashMap<>();
        List<Process> appiumProcs = new ArrayList<>();

        int autoPort = 4730;
        for (String deviceName : deviceToTagExpr.keySet()) {
            // Select port: first device uses 4723 if only one; otherwise allocate incrementally
            int port = (deviceToTagExpr.size() == 1) ? 4723 : autoPort++;
            String deviceId = filtered.get(deviceName);
            if (!connected.contains(deviceId) && looksLikeIpPort(deviceId)) {
                AdbUtils.connect(deviceId);
            }
            Process p = serverManager.startAppium(port);
            if (p == null) throw new RuntimeException("❌ " + deviceName + " için Appium başlatılamadı!");
            appiumProcs.add(p);
            devicePorts.put(deviceName, port);
        }

        // Persist device_ports.json
        JsonUtils.writeJsonFileRelative("device_ports.json", new LinkedHashMap<>(devicePorts));

        // Run Cucumber in threads
        List<Thread> threads = new CopyOnWriteArrayList<>();
        for (Map.Entry<String, String> entry : deviceToTagExpr.entrySet()) {
            String deviceName = entry.getKey();
            String tagExpr = entry.getValue();
            Thread t = new Thread(() -> runCucumberFor(tagExpr, deviceName));
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) t.join();

        // stop appium
        serverManager.shutdownAll();
    }

    private static void runCucumberFor(String tagExpr, String platformName) {
        System.out.println("🔎 " + platformName.toUpperCase() + " için Cucumber: --tags=\"" + tagExpr + "\"");
        // Set platform for this thread
        DriverManager.setPlatform(platformName);
        // Invoke Cucumber programmatically
        List<String> argv = new ArrayList<>();
        argv.add("--plugin"); argv.add("pretty");
        argv.add("--plugin"); argv.add("summary");
        argv.add("--glue"); argv.add("com.confautomation.steps");
        argv.add("--glue"); argv.add("com.confautomation.hooks");
        argv.add("--tags"); argv.add(tagExpr);
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

    private static boolean looksLikeIpPort(String udid) {
        if (udid == null) return false;
        String compact = udid.replace(".", "").replace(":", "");
        return udid.contains(":") && !compact.isBlank() && compact.chars().allMatch(Character::isDigit);
    }

    private static String safeReadLine(BufferedReader br) throws IOException {
        String line = br.readLine();
        return line == null ? null : line.trim();
    }

    private static String askTagExpression(BufferedReader br, List<String> discoveredTags) throws IOException {
        System.out.println("Tag ifadesi girin veya numara(lar) seçin");
        System.out.println("  Örnek ifade: @smoke and not @wip");
        System.out.print("  Seçim (örn: 1 veya 1,3) ya da ifade: ");
        String inp = safeReadLine(br);
        if (inp == null || inp.isBlank()) {
            return "@smoke"; // reasonable default
        }
        // If contains '@' or boolean keywords, treat as raw expression
        String lower = inp.toLowerCase(Locale.ROOT);
        if (inp.contains("@") || lower.contains(" and ") || lower.contains(" or ") || lower.contains(" not ")) {
            return inp;
        }
        // Otherwise, parse numbers
        List<String> picked = new ArrayList<>();
        String[] parts = inp.split(",");
        for (String p : parts) {
            try {
                int idx = Integer.parseInt(p.trim());
                if (idx >= 1 && idx <= discoveredTags.size()) picked.add(discoveredTags.get(idx - 1));
            } catch (NumberFormatException ignored) {}
        }
        if (picked.isEmpty()) return "@smoke";
        // Ask join operator
        System.out.print("Seçili tagları nasıl birleştireyim? [or/and] (varsayılan: or): ");
        String join = safeReadLine(br);
        String op = (join != null && join.equalsIgnoreCase("and")) ? " and " : " or ";
        if (picked.size() == 1) return picked.get(0);
        return "(" + String.join(op, picked) + ")";
    }

    private static List<String> discoverTags() {
        List<Path> roots = List.of(
                Path.of("java", "src", "main", "resources", "features"),
                Path.of("src", "main", "resources", "features"),
                Path.of("java", "target", "classes", "features"),
                Path.of("target", "classes", "features"),
                Path.of("features")
        );
        Set<String> tags = new TreeSet<>();
        for (Path root : roots) {
            try {
                if (!Files.exists(root)) continue;
                Files.walk(root)
                        .filter(p -> p.toString().endsWith(".feature"))
                        .forEach(p -> extractTagsFromFile(p, tags));
            } catch (IOException ignored) {}
        }
        return new ArrayList<>(tags);
    }

    private static void extractTagsFromFile(Path file, Set<String> out) {
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (String l : lines) {
                String s = l.strip();
                if (s.startsWith("@")) {
                    // split by whitespace
                    for (String tok : s.split("\\s+")) {
                        if (tok.startsWith("@") && tok.length() > 1) out.add(tok);
                    }
                }
            }
        } catch (IOException ignored) {}
    }
}
