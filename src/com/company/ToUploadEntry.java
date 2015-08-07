package com.company;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ToUploadEntry extends Entry {
    private final String shortPath;
    private final File file;

    public String getShortPath() {
        return shortPath;
    }

    public File getFile() {
        return file;
    }

    public ToUploadEntry(File file, boolean folder, String megaPath, String shortPath, ZonedDateTime createdInMega) {
        super(folder, megaPath, createdInMega);
        this.file = file;
        this.shortPath = shortPath;
    }

    @Override
    public String toString() {
        String inMega = "";
        String createdInMegaString = "";
        String isNew = "NEW ";
        if (createdInMega != null) {
            inMega = createdInMega.toInstant().toString();
            createdInMegaString = "created in mega: " + inMega + ' ';
            isNew = "OLD ";
        }

        return "ToUploadEntry {" + isNew +
                "megaPath: " + megaPath + ' ' +
                "isFolder: " + folder   + ' ' +
                "shortPath: " + shortPath + ' ' +
                createdInMegaString +
                "local modified: " + Instant.ofEpochMilli(file.lastModified()) + ' ' +
//                ", file=" + file +
        '}';
    }
}
