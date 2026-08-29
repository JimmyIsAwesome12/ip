package lebron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link DateTime}, the date/time value object. {@code parse} is
 * the highest-value method here: it accepts several input formats, silently
 * falls through them, and throws on anything it cannot recognise.
 */
public class DateTimeTest {

    // ---- parse: accepted formats -------------------------------------------------

    @Test
    public void parse_isoDate_dateOnly() {
        assertEquals("Oct 15 2019", DateTime.parse("2019-10-15").toString());
    }

    @Test
    public void parse_isoDateTime_dateAndTime() {
        assertEquals("Dec 02 2019 6:00pm", DateTime.parse("2019-12-02 1800").toString());
    }

    @Test
    public void parse_dayFirstDate_dateOnly() {
        assertEquals("Dec 02 2019", DateTime.parse("2/12/2019").toString());
    }

    @Test
    public void parse_dayFirstDateTime_dateAndTime() {
        // The exact example from the Level-8 requirement.
        assertEquals("Dec 02 2019 6:00pm", DateTime.parse("2/12/2019 1800").toString());
    }

    @Test
    public void parse_surroundingWhitespace_trimmedAndParsed() {
        assertEquals("Oct 15 2019", DateTime.parse("  2019-10-15  ").toString());
    }

    // ---- parse: rejected input -------------------------------------------------

    @Test
    public void parse_unrecognisedText_throws() {
        assertThrows(IllegalArgumentException.class, () -> DateTime.parse("next tuesday"));
    }

    @Test
    public void parse_emptyString_throws() {
        assertThrows(IllegalArgumentException.class, () -> DateTime.parse(""));
    }

    @Test
    public void parse_impossibleCalendarDate_throws() {
        assertThrows(IllegalArgumentException.class, () -> DateTime.parse("2019-13-40"));
    }

    @Test
    public void parse_dateWithBadTime_throws() {
        assertThrows(IllegalArgumentException.class, () -> DateTime.parse("2019-12-02 2500"));
    }

    // ---- toString: display formatting -----------------------------------------

    @Test
    public void toString_midnight_shows12am() {
        assertEquals("Jan 01 2020 12:00am", DateTime.parse("2020-01-01 0000").toString());
    }

    @Test
    public void toString_noon_shows12pm() {
        assertEquals("Jan 01 2020 12:00pm", DateTime.parse("2020-01-01 1200").toString());
    }

    // ---- toFileFormat: canonical storage form --------------------------------

    @Test
    public void toFileFormat_dateOnly_isoDate() {
        assertEquals("2019-10-15", DateTime.parse("2019-10-15").toFileFormat());
    }

    @Test
    public void toFileFormat_dayFirstInput_normalisedToIso() {
        assertEquals("2019-10-15", DateTime.parse("15/10/2019").toFileFormat());
    }

    @Test
    public void toFileFormat_withTime_isoDateAndTime() {
        assertEquals("2019-12-02 1800", DateTime.parse("2/12/2019 1800").toFileFormat());
    }

    @Test
    public void parseThenToFileFormat_reparsesToSameValue() {
        String canonical = DateTime.parse("2/12/2019 1800").toFileFormat();
        assertEquals("Dec 02 2019 6:00pm", DateTime.parse(canonical).toString());
    }
}
