# MegaSync
Java Command line Sync Client for Mega.nz

currently uploads only files that do not exist in mega

#Requirements

Windows version of megatools-1.9.95

#How to run

Open terminal where the jar file is contained /dist

#Usage

    java -jar megaSync.jar <email> <path-to-tools> <path-to-sync-with-/Root>

#Run Example:

    java -jar megaSync.jar some@mail.com C:\apps\megatools-1.9.95-win64\ "C:\Users\User\Documents\mega"
    
#TODOs

* show ERRORS from actual command executions
* improve sync strategy - not buffered sync, but try sort-of streamed sync
* improve upload - not file by file (try creating folders first - till there are no folders. Start by creating the most top level folders. Rescan/recompare after each folder create - or delete all files in the toUploadList under the specific folder. or even create a tree structure of the entries.)
* overwrite function
* download files - more advanced sync
* dry run as argument
* verbose as argument
* gui client
