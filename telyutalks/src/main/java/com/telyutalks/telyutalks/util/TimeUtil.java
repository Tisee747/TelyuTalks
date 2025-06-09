package com.telyutalks.telyutalks.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtil {
    public static String getTimeAgo(LocalDateTime pastTime) {
        if (pastTime == null) return "";
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(pastTime, now);
        long seconds = duration.getSeconds();
        if (seconds < 60) return seconds + " detik yang lalu";
        long minutes = duration.toMinutes();
        if (minutes < 60) return minutes + " menit yang lalu";
        long hours = duration.toHours();
        if (hours < 24) return hours + " jam yang lalu";
        long days = duration.toDays();
        if (days < 30) return days + " hari yang lalu";
        long months = days / 30;
        if (months < 12) return months + " bulan yang lalu";
        long years = months / 12;
        return years + " tahun yang lalu";
    }
}
