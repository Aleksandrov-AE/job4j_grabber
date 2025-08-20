package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HabrCareerDateTimeParserTest {
    private final HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();

    @Test
    void parseWhenBlank() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(""));
    }

    @Test
    void whenParsThenReturnLocalDateTime() {
        String input = "2025-08-19T10:32:00+03:00";
        LocalDateTime localDateTime = parser.parse(input);
        assertEquals(LocalDateTime.of(2025, 8, 19, 10, 32, 0), localDateTime);
    }
}