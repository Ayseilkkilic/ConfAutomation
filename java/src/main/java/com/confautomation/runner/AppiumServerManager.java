package com.confautomation.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;

public class AppiumServerManager {
    private final List<Process> processes = new ArrayList<>();

    public Process startAppium(int port) throws IOException {
        System.out.println("Appium sunucusu " + port + " portunda başlatılıyor...");
        ProcessBuilder pb = new ProcessBuilder("appium", "--port", String.valueOf(port));
        // Ensure Android SDK environment is available to the Appium process
        ensureAndroidSdkEnv(pb);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        processes.add(p);
        if (waitUntilUp(port, Duration.ofSeconds(30))) {
            System.out.println("✅ Appium sunucusu " + port + " portunda başarıyla başlatıldı!");
            return p;
        } else {
            System.out.println("❌ Appium sunucusu " + port + " portunda başlatılamadı!");
            return null;
        }
    }

    private void ensureAndroidSdkEnv(ProcessBuilder pb) {
        Map<String, String> env = pb.environment();
        String sdk = firstNonEmpty(System.getenv("ANDROID_SDK_ROOT"), System.getenv("ANDROID_HOME"), detectSdkByConvention());
        if (sdk != null && !sdk.isBlank()) {
            env.putIfAbsent("ANDROID_SDK_ROOT", sdk);
            env.putIfAbsent("ANDROID_HOME", sdk);
            // Prepend platform-tools and emulator to PATH for adb/emulator
            String path = env.getOrDefault("PATH", "");
            String sep = File.pathSeparator;
            String extras = sdk + File.separator + "platform-tools" + sep + sdk + File.separator + "emulator";
            if (!path.contains(extras)) {
                env.put("PATH", extras + sep + path);
            }
            System.out.println("ℹ️ ANDROID_SDK_ROOT=" + sdk + " ile Appium başlatılıyor.");
        } else {
            System.out.println("⚠️ ANDROID_SDK_ROOT/ANDROID_HOME bulunamadı. Appium oturumu başlatılamayabilir.");
        }
    }

    private String detectSdkByConvention() {
        String home = System.getProperty("user.home");
        Path mac = Path.of(home, "Library", "Android", "sdk");
        if (Files.exists(mac)) return mac.toString();
        Path linux = Path.of(home, "Android", "Sdk");
        if (Files.exists(linux)) return linux.toString();
        return null;
    }

    private static String firstNonEmpty(String... vals) {
        if (vals == null) return null;
        for (String v : vals) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    private boolean waitUntilUp(int port, Duration timeout) {
        long end = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < end) {
            try {
                URL url = new URL("http://localhost:" + port + "/status");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(2000);
                con.setReadTimeout(2000);
                con.setRequestMethod("GET");
                int code = con.getResponseCode();
                if (code == 200) return true;
            } catch (IOException ignored) {}
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
        return false;
    }

    public void shutdownAll() {
        for (Process p : processes) {
            try { p.destroy(); } catch (Exception ignored) {}
        }
        System.out.println("🚪 Appium sunucuları kapatıldı!");
    }
}
