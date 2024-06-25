package ru.job4j.grabber.utils;

import org.junit.Test;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HabrCareerDateTimeParserTest {

    @Test
    public void whenEmptyString() {
        String emptyString = "";
        LocalDateTime parsedDate = new HabrCareerDateTimeParser().parse(emptyString);
        assertNull(parsedDate);
    }

    @Test
    public void whenParse() {
        String dateString = "2022-01-21T13:30:00";
        LocalDateTime dateTime = LocalDateTime.of(2022, 1, 21, 13, 30, 0);
        LocalDateTime rsl = new HabrCareerDateTimeParser().parse(dateString);
        assertEquals(dateTime, rsl);
    }

    @Test
    public void parseInvalidString() {
        String dateString = "1020-07-33T11:85:60";
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        LocalDateTime result = parser.parse(dateString);
        assertNull(result);
    }
}