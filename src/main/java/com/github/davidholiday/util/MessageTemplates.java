package com.github.davidholiday.util;

import java.text.MessageFormat;

public class MessageTemplates {

    public static String getCountDeltaErrorMessage(int expected, String inOfWhat,  int actual) {
        return MessageFormat.format("expected {0} {1} but found {2}", expected, inOfWhat, actual);
    }

    public static String getCountDeltaErrorMessage(String expected, String inOfWhat, String actual) {
        return MessageFormat.format("expected {0} {1} but found {2}", expected, inOfWhat, actual);
    }

}
