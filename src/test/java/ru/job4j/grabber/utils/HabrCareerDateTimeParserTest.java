package ru.job4j.grabber.utils;

import org.junit.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HabrCareerDateTimeParserTest {

    @Test
    public void whenParse() {
        LocalDateTime date = LocalDateTime.now();
        String text = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime parsedDate = new HabrCareerDateTimeParser().parse(text);
        assertEquals(date, parsedDate);
    }

    @Test
    public void whenParseEmptyString() {
        String emptyString = "";
        LocalDateTime parsedDate = new HabrCareerDateTimeParser().parse(emptyString);
        assertNull(parsedDate);
    }
}