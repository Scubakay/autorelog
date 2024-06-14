package com.scubakay.autorelog.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class Config extends MidnightConfig {
    @Entry(name="Delay") public static int delay = 60;
    @Entry(name="Interval") public static int interval = 30;
    @Entry(name = "Max Attempts") public static int maxAttempts = 5;
    @Entry(name = "AFK Delay") public static int afkDelay = 60;
    @Entry(name = "AFK Detection") public static boolean afkDetection = true;
    @Entry(name = "Logging") public static boolean logging = false;
}
