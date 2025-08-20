package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        if (parse == null || parse.isBlank()) {
            throw new IllegalArgumentException("Дата пуста");
        }
        return OffsetDateTime.parse(parse, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .toLocalDateTime();
    }

}