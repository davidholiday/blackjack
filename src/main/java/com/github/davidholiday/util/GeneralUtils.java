package com.github.davidholiday.util;

import java.util.concurrent.ThreadLocalRandom;

public class GeneralUtils {

    public static final int DECK_SIZE_NO_JOKERS = 52;

    public static final int DECK_SIZE_WITH_JOKERS = 54;

    public static int getRandomIntForRange(int floor, int ceiling) {
        return ThreadLocalRandom.current().nextInt(floor, ceiling + 1);
    }


}
