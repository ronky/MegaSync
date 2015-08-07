package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static boolean detailedLog = false;
    private static boolean dryRun = false;

    private static LOG log = new LOG();

    private static String pathToTools;

    private static String pathToSync;

    // TODO show ERRORS from actual command executions
    // todo improve sync strategy - try sort of streamed sync
    // todo improve upload - not file by file (try creating folders first - till there are no folders. Start by creating the most top level folders. Rescan/recompare after each folder create - or delete all files in the toUploadList under the specific folder. or even create a tree structure of the entries.)

    public static void main(String[] args) {
        String userName = getUsername(args);
        loadPaths(args);
        String pwd = loadPasswordFromConsole();

        ArrayList<String> loadEntriesWarnings = new ArrayList<>();
        ArrayList<String> uploadWarnings = new ArrayList<>();

        beginSync(userName, pwd, loadEntriesWarnings, uploadWarnings);
    }

    private static void loadPaths(String[] args) {
        if (args.length != 3) {
            log.error("incorrect argumets. Length: " + args.length + " Use: <login-email> <path-to-tools> <path-to-sync>");

            for (int i = 0; i < args.length; i++) {
                log.info("arg " + i + " :" + args[i]);
            }

            System.exit(1);
        }
        pathToTools = args[1];
        pathToSync = args[2];
        log.info("Tools: " + pathToTools);
        log.info("Sync Folder: " + pathToSync);
    }

    private static void beginSync(String userName, String pwd, ArrayList<String> loadEntriesWarnings, ArrayList<String> uploadWarnings) {
        List<MegaEntry> megaEntries = loadMegaEntries(userName, pwd, loadEntriesWarnings);
        List<ToUploadEntry> toUploadEntries = compareLocalFilesWithMega(megaEntries);

        logEntriesToUpload(toUploadEntries);
        logWarnings(loadEntriesWarnings, "there were warnings during obtaining list to upload: ", "no warnings during obtaining files form mega.");
        log.debug("to upload entries count: " + toUploadEntries.size());

        uploadEntries(userName, pwd, toUploadEntries, uploadWarnings);

        logWarnings(uploadWarnings, "there were warnings during upload: ", "no warnings during upload.");
    }


    private static void logWarnings(ArrayList<String> uploadWarnings, String warningsPresentMessage, String noWarningsMessage) {
        if (!uploadWarnings.isEmpty()) {
            log.warn(warningsPresentMessage);
            for (String warning : uploadWarnings) {
                log.warn(warning);
            }
        } else {
            log.info(noWarningsMessage);
        }
    }

    private static void uploadEntries(String userName, String pwd, List<ToUploadEntry> toUploadEntries, ArrayList<String> uploadWarnings) {
        MegaEntryUpLoader megaEntryUpLoader = new MegaEntryUpLoader(pathToTools);
        megaEntryUpLoader.uploadEntries(userName, pwd, uploadWarnings, toUploadEntries);
    }

    private static void logEntriesToUpload(List<ToUploadEntry> toUploadEntries) {
        if (detailedLog)
            for (ToUploadEntry toUploadEntry : toUploadEntries) {
                log.debug(toUploadEntry.toString());
            }
    }

    private static List<ToUploadEntry> compareLocalFilesWithMega(List<MegaEntry> megaEntries) {
        LocalFileLister lister = new LocalFileLister(megaEntries, new File(pathToSync), detailedLog);
        lister.listRecursive(new File(pathToSync));

        return lister.getToUploadEntries();
    }

    private static List<MegaEntry> loadMegaEntries(String userName, String pwd, ArrayList<String> loadEntriesWarnings) {
        MegaEntryLoader megaLoader = new MegaEntryLoader(pathToTools, detailedLog, dryRun);
        return megaLoader.loadEntries(userName, pwd, loadEntriesWarnings);
    }

    private static String getUsername(String[] args) {
        /** Check arguments */
        if (args.length < 1) {
            log.error("ERROR args");
            System.exit(1);
        }
        log.info("User Name: " + args[0]);
        return args[0];
    }

    private static String loadPasswordFromConsole() {
        System.out.print("Password: ");
        Console console = System.console();
        if (console == null) {
            log.error("Couldn't get Console instance");
            System.exit(1);
        }
        char passwordArray[] = console.readPassword("Enter your secret password: ");
        return new String(passwordArray);
    }


}
