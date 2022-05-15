package com.store.app.departmentactivitymonitor.serialization;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StringToInstantParser {

    private static final String[] SUPPORTED_PATTERNS = {
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss.SS",
            "yyyy-MM-dd'T'HH:mm:ss.S",
            "yyyy-MM-dd'T'HH:mm:ss"
    };

    private final List<InstantParser> parsers;

    public StringToInstantParser() {
        parsers = new ArrayList<>();

        // try default Instant parser first
        parsers.add(Instant::parse);

        // then try the supported formats (in order)
        for (String pattern : SUPPORTED_PATTERNS) {
            DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
            parsers.add(s -> LocalDateTime.parse(s, f).toInstant(ZoneOffset.UTC));
        }
    }

    public Instant parse(String value) throws ParseException {

        int errorIndex = 0;
        for (InstantParser parser : parsers) {
            try {
                return parser.parse(value);
            } catch (DateTimeParseException e) {
                errorIndex = e.getErrorIndex();
            }
        }

        throw new ParseException(String.format("Unable to convert '%s' to Instant", value), errorIndex);
    }

    @FunctionalInterface
    private interface InstantParser {

        Instant parse(String s) throws DateTimeParseException;
    }
}
