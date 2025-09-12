package com.confautomation.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdbUtils {

    public static List<String> connectedDevices() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("adb", "devices");
        Process p = pb.start();
        List<String> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.endsWith("\tdevice")) {
                    String id = line.split("\t")[0];
                    result.add(id.trim());
                }
            }
        }
        return result;
    }

    public static boolean connect(String ipPort) {
        try {
            ProcessBuilder pb = new ProcessBuilder("adb", "connect", ipPort);
            Process p = pb.start();
            p.waitFor(2, TimeUnit.SECONDS);
            // Re-check
            return connectedDevices().contains(ipPort);
        } catch (Exception e) {
            return false;
        }
    }
}

