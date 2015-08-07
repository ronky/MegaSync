package com.company;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MegaEntry extends Entry {

    public static MegaEntry fromStrings(String isFolder, String date, String time, String path) {
        boolean folder = false;
        // 1 yes - 0 false
        if ("1".equals(isFolder)) {
            folder = true;
        }
        String datetime = date + " " + time;
        String datetimeforInstant = date + "T" + time;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());;
        ZonedDateTime createdInMega = ZonedDateTime.parse(datetime, formatter);
        String megaPath = path;

        return new MegaEntry(folder, megaPath, createdInMega);
    }

    private MegaEntry(boolean folder, String megaPath, ZonedDateTime createdInMega) {
        super(folder, megaPath, createdInMega);
    }

    @Override
    public String toString() {
        return "MegaEntry{" +
                "isFolder=" + folder +
                ", megaPath='" + megaPath + '\'' +
                ", createdInMega=" + createdInMega +
                '}';
    }
}
