package com.ozdece.gheasy.datetime;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class ZoneBasedDateTimeFormatter {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault());

    public static String toFormattedString(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(dateTimeFormatter);
    }

}
