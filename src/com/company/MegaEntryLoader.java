package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MegaEntryLoader {
    private final boolean detailedLog;
    private final boolean dryRun;

    private final String pathToTools;
    private static LOG log = new LOG();

    public MegaEntryLoader(String pathToTools, boolean detailedLog, boolean dryRun) {
        this.pathToTools = pathToTools;
        this.detailedLog = detailedLog;
        this.dryRun = dryRun;
    }

    public List<MegaEntry> loadEntries(String userName, String pwd, ArrayList<String> warnings) {
        List<MegaEntry> entriesToReturn = null;

        String listEntriesCommand = pathToTools + "megals.exe -hnlR --header -u " + userName + " -p " + pwd + " /Root";
        if (dryRun) {
            log.debug("DRY GET: " + listEntriesCommand);
        } else {
            try {
                Process process = Runtime.getRuntime().exec(listEntriesCommand);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                entriesToReturn = loadEntriesFromReader(reader, warnings);

                process.waitFor();
                reader.close();
            } catch (IOException e) {
                log.error("error running listEntriesCommand: " + listEntriesCommand);
            } catch (InterruptedException e) {
                log.error("error running listEntriesCommand: " + listEntriesCommand + " InterruptedException");
            }
        }
        return entriesToReturn;
    }

    private List<MegaEntry> loadEntriesFromReader(BufferedReader reader, List<String> warnings) throws IOException {
        List<MegaEntry> entriesToReturn = new ArrayList<>();

        String line;
        int lineCount = -1;
        while ((line = reader.readLine()) != null) {
            lineCount++;

            if (detailedLog)
                log.debug(line);

            if (!line.startsWith("===") && !line.startsWith("Handle")) {
                String[] splittedByMoreSpaces = line.split("\\s{2,}");

                if (splittedByMoreSpaces[1].equals("2")) {
                    warnings.add("Skipping Type 2 line." + "\r\nLine " + lineCount + ": " + line);
                    continue;
                }

                if (splittedByMoreSpaces.length != 3) {
                    warnings.add("Unknown multiple space line split. Number of tokens: " + splittedByMoreSpaces.length + "\r\nLine " + lineCount + ": " + line);
                    continue;
                } else {
                    String[] ownerAndIsFolder = splittedByMoreSpaces[1].split("\\s{1,}");
                    String isFolder;
                    if (ownerAndIsFolder.length == 2) {
                        isFolder = ownerAndIsFolder[1];
                    } else if (ownerAndIsFolder.length == 1) {
                        isFolder = ownerAndIsFolder[0];
                    } else {
                        warnings.add("Unknown ownerAndIsFolder. Number of tokens: " + ownerAndIsFolder.length + "\r\nLine " + lineCount + ": " + line);
                        continue;
                    }

                    String path;
                    String date;
                    String time;

                    if (splittedByMoreSpaces[2].split("\\s{1,}").length < 4) {
                        warnings.add("Unsupported number of tokens reader sizeDateTimeFilepathname. Less than 4. " + "\r\nLine " + lineCount + ": " + line);
                        continue;
                    }

                    String sizeDateTimeFilepathnameTogether = splittedByMoreSpaces[2];
                    String dateTimeFilepathnameTogether;

                    if (sizeDateTimeFilepathnameTogether.startsWith("-")) {
                        /** skip size as dash */
                        int firstSpace = sizeDateTimeFilepathnameTogether.indexOf(" ");
                        dateTimeFilepathnameTogether = sizeDateTimeFilepathnameTogether.substring(firstSpace + 1);
                    } else {
                        /** skip size  */
                        int firstSpace = sizeDateTimeFilepathnameTogether.indexOf(" ");
                        dateTimeFilepathnameTogether = sizeDateTimeFilepathnameTogether.substring(firstSpace + 1);
                        /** skip units*/
                        int secondSpace = dateTimeFilepathnameTogether.indexOf(" ");
                        dateTimeFilepathnameTogether = dateTimeFilepathnameTogether.substring(secondSpace + 1);
                    }

                    int date_timeSpace = dateTimeFilepathnameTogether.indexOf(" ");
                    date = dateTimeFilepathnameTogether.substring(0, date_timeSpace);
                    String timeFilepathnameTogether = dateTimeFilepathnameTogether.substring(date_timeSpace + 1);

                    int time_filepathnameSpace = timeFilepathnameTogether.indexOf(" ");
                    time = timeFilepathnameTogether.substring(0, time_filepathnameSpace);
                    path = timeFilepathnameTogether.substring(time_filepathnameSpace + 1);

                    entriesToReturn.add(MegaEntry.fromStrings(isFolder, date, time, path));
                }
            } else {
                warnings.add("Skipped header." + "\r\nLine " + lineCount + ": " + line);
                continue;
            }
        }
        return entriesToReturn;
    }
}
