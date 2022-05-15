package com.github.davidholiday.util;

import java.lang.management.ManagementFactory;


public class RuntimeInfo {

    public final boolean ASSERTIONS_ENABLED;

    public RuntimeInfo() {
        // t/y SO https://stackoverflow.com/a/16788186
        this.ASSERTIONS_ENABLED = ManagementFactory.getRuntimeMXBean().getInputArguments().contains("-ea");
    }

}
