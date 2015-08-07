package com.company;

import java.io.File;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class LocalFileLister {
    private final boolean detailedLog;

    private static final LOG log = new LOG();
    private final List<MegaEntry> entries;
    private final File baseFile;

    private List<ToUploadEntry> toUploadEntries;

    public LocalFileLister(List<MegaEntry> megaEntries, File dir, boolean detailedLog) {
        this.entries = megaEntries;
        baseFile = dir;
        toUploadEntries = new ArrayList<>();
        this.detailedLog = detailedLog;
    }

    /**
     * This method recursively lists all
     * .txt and .java files in a directory
     */
    public void listRecursive(File dir) {
        Arrays.stream(dir.listFiles((f, n) ->
                        !n.startsWith(".") && f.isDirectory()
        ))
                .forEach(unchecked((file) -> {
                    String subPathName = file.getCanonicalPath()
                            .substring(baseFile
                                    .getCanonicalPath()
                                    .length());

                    if (detailedLog)
                        log.debug(subPathName);

                    if (file.isDirectory()) {
                        ToUploadEntry toUploadEntry = searchMatch(file, subPathName);
                        if (toUploadEntry != null)
                            toUploadEntries.add(toUploadEntry);
                        listRecursive(file);
                    } else {
                        ToUploadEntry toUploadEntry = searchMatch(file, subPathName);
                        if (toUploadEntry != null)
                            toUploadEntries.add(toUploadEntry);
                    }
                }));
    }

    private ToUploadEntry searchMatch(File file, String subPathName) {
        String toComparePath = "/Root" + subPathName.replace('\\', '/');
        MegaEntry match = null;
        // todo use java 8 lambda ?
        for (MegaEntry me : entries) {
            if (me.getMegaPath().equals(toComparePath)) {
                if (detailedLog)
                    log.debug("Found match: " + toComparePath);
                match = me;
            }
        }

        /** add only files which are locally modified later than they were created in mega */

        if (match != null) {
            if (match.isFolder()) {
                return null;
            }
            Instant localModifiedInstant = Instant.ofEpochMilli(file.lastModified());
            if (match.getCreatedInMega().toInstant().compareTo(localModifiedInstant) == 0) {
                log.warn("created on mega the same time as modified ?!");
                return null;
            } else if (match.getCreatedInMega().toInstant().isBefore(localModifiedInstant)) {
                // mega file created is before filesystem modified

                if (detailedLog) {
                    log.debug("mega date before");
                    log.debug(toComparePath);
                    log.debug("local file date: " + new Date(file.lastModified()));
                    log.debug("mega  file date: " + match.getCreatedInMega());
                }

                return new ToUploadEntry(file, !file.isFile(), match.getMegaPath(), subPathName, match.getCreatedInMega());
            } else {
                // mega created is newer as last modified locally
                if (detailedLog) {
                    log.debug("mega date newer");
                    log.debug("local file date: " + new Date(file.lastModified()));
                    log.debug("local file date localModifiedInstant: " + localModifiedInstant);
                    log.debug("mega  file date: " + match.getCreatedInMega());
                    log.debug("mega  file date as instant: " + match.getCreatedInMega().toInstant());
                }
                return null;
            }
        } else {
            if (detailedLog) {
                log.debug("no match for: " + subPathName);
                log.debug("no match for as match to path: " + toComparePath);
            }
            return new ToUploadEntry(file, !file.isFile(), toComparePath, subPathName, null);
        }
    }

    /**
     * This utility simply wraps a functional
     * interface that throws a checked exception
     * into a Java 8 Consumer
     */
    private <T> Consumer<T> unchecked(CheckedConsumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @FunctionalInterface
    private interface CheckedConsumer<T> {
        void accept(T t) throws Exception;
    }

    public List<ToUploadEntry> getToUploadEntries() {
        return toUploadEntries;
    }
}
