package com.kustaurant.crawler.global.util;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SystemMemoryUtils {

    public static double getSystemMemoryUsage() {
        MemoryStat stat = resolveMemoryStat();
        return stat.total == 0 ? 0.0 : (double) (stat.total - stat.available) / stat.total;
    }

    public static long getAvailableMemory() {
        return resolveMemoryStat().available;
    }

    /* ===============================
       Core Resolver
       =============================== */

    private static MemoryStat resolveMemoryStat() {
        if (isLinuxLike()) {
            return readLinuxMemInfo();
        }
        return readJvmMemory();
    }

    /* ===============================
       OS / Profile 판단
       =============================== */

    private static boolean isLinuxLike() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("linux")) return true;
        if (os.contains("windows")) return false;

        // OS 판별이 애매한 경우 → prod 프로필이면 Linux 취급
        String profile = System.getProperty("spring.profiles.active", "");
        return profile.contains("prod");
    }

    /* ===============================
       Windows / 기타 OS
       =============================== */

    private static MemoryStat readJvmMemory() {
        OperatingSystemMXBean osBean =
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        long total = osBean.getTotalPhysicalMemorySize();
        long available = osBean.getFreePhysicalMemorySize(); // Windows에선 available 의미
        return new MemoryStat(total, available);
    }

    /* ===============================
       Linux 전용 (MemAvailable)
       =============================== */

    private static MemoryStat readLinuxMemInfo() {
        try {
            List<String> lines = Files.readAllLines(Path.of("/proc/meminfo"));

            long totalKb = -1;
            long availableKb = -1;

            for (String line : lines) {
                if (line.startsWith("MemTotal:")) {
                    totalKb = parseKb(line);
                } else if (line.startsWith("MemAvailable:")) {
                    availableKb = parseKb(line);
                }
            }

            if (totalKb <= 0 || availableKb < 0) {
                throw new IllegalStateException("MemTotal or MemAvailable not found");
            }

            return new MemoryStat(totalKb * 1024, availableKb * 1024);

        } catch (Exception e) {
            // Linux인데 실패하면 안전하게 JVM 방식으로 fallback
            return readJvmMemory();
        }
    }

    private static long parseKb(String line) {
        // 예: "MemAvailable:  123456 kB"
        String[] parts = line.split("\\s+");
        for (String p : parts) {
            if (p.matches("\\d+")) return Long.parseLong(p);
        }
        return -1;
    }

    /* ===============================
       Value Object
       =============================== */

    private static class MemoryStat {
        final long total;
        final long available;

        MemoryStat(long total, long available) {
            this.total = total;
            this.available = available;
        }
    }
}
