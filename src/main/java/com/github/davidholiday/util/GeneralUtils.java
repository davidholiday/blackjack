package com.github.davidholiday.util;

import java.util.concurrent.ThreadLocalRandom;

public class GeneralUtils {

    public static int getRandomIntForRange(int floor, int ceiling) {
        return ThreadLocalRandom.current().nextInt(floor, ceiling + 1);
    }


}
