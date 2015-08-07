package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MegaEntryUpLoader {
    // TODO make argument / config
    private boolean dryRun = false;

    String pathToTools;
    private static LOG log = new LOG();

    public MegaEntryUpLoader(String pathToTools) {
        this.pathToTools = pathToTools;
    }

    public void uploadEntries(String userName, String pwd, ArrayList<String> warnings, List<ToUploadEntry> toUploadEntries) {
        for (ToUploadEntry entry : toUploadEntries) {

            if (entry.isFolder()) {
                String createFolderCommand = pathToTools + "megamkdir .exe -u " + userName + " \"" + entry.getMegaPath() + "\"";
                if (dryRun) {
                    log.debug("DRY Create folder: " + createFolderCommand);
                } else {
                    log.debug("REAL Create folder: " + entry.getFile().getPath());
                    executeCommand(warnings, createFolderCommand, pwd);
                }
            } else {

                String uploadFileCommand = pathToTools + "megaput.exe -u " + userName + " --path \"" + entry.getMegaPath() + "\" \"" + entry.getFile().getPath() + "\"";
                if (dryRun) {
                    log.debug("DRY Exec: " + uploadFileCommand);
                } else {
                    log.debug("REAL Exec: " + uploadFileCommand);
                    executeCommand(warnings, uploadFileCommand, pwd);
                }
            }
        }
    }

    private void executeCommand(ArrayList<String> warnings, String command, String pwd) {
        command += " -p " + pwd;
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                log.debug(line);
            }

            process.waitFor();
            reader.close();
        } catch (IOException e) {
            log.error("error running command: " + command);
        } catch (InterruptedException e) {
            log.error("error running command: " + command + " InterruptedException");
        }
    }
}
