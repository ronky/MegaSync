package com.company;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public abstract class Entry {
    protected final boolean folder;

    protected final String megaPath;

    protected final ZonedDateTime createdInMega;

    protected Entry(boolean folder, String megaPath, ZonedDateTime createdInMega) {
        this.folder = folder;
        this.megaPath = megaPath;
        this.createdInMega = createdInMega;
    }

    public boolean isFolder() {
        return folder;
    }

    public String getMegaPath() {
        return megaPath;
    }

    public ZonedDateTime getCreatedInMega() {
        return createdInMega;
    }

}
