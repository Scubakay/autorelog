package com.scubakay.autorelog.config;

import com.scubakay.autorelog.util.AfkMode;
import com.scubakay.autorelog.util.Logging;
import eu.midnightdust.lib.config.MidnightConfig;

public class Config extends MidnightConfig {
    @Entry() public static int interval = 30;
    @Entry() public static int maxAttempts = 5;

    @Comment(centered = true) public static Comment afkDetectionComment;
    @Entry() public static int afkDelay = 300;
    @Entry() public static AfkMode mode = AfkMode.AUTOMATIC;

    @Comment(centered = true) public static Comment loggingComment;
    @Entry() public static Logging logging = Logging.DISABLED;
}
