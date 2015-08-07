package com.company;

public class LOG {
    public void debug(String toLog) {
        System.out.println("Debug: " + toLog);
    }
    public void info(String toLog) {
        System.out.println("Info: " + toLog);
    }
    public void warn(String toLog) {
        System.out.println("WARN: " + toLog);
    }
    public void error(String toLog) {
        System.out.println("!!! ERROR: " + toLog);
    }
}
