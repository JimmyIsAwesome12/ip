package lebron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests for {@link Deadline}'s display string and its data-file encoding. */
public class DeadlineTest {

    @Test
    public void toString_dateOnly_showsFormattedDate() {
        Deadline deadline = new Deadline("return book", DateTime.parse("2019-12-02"));
        assertEquals("[D][ ] return book (by: Dec 02 2019)", deadline.toString());
    }

    @Test
    public void toString_withTime_showsFormattedDateTime() {
        Deadline deadline = new Deadline("submit report", DateTime.parse("2019-12-02 1800"));
        assertEquals("[D][ ] submit report (by: Dec 02 2019 6:00pm)", deadline.toString());
    }

    @Test
    public void toString_done_showsCrossInStatusBox() {
        Deadline deadline = new Deadline("return book", DateTime.parse("2019-12-02"));
        deadline.markAsDone();
        assertEquals("[D][X] return book (by: Dec 02 2019)", deadline.toString());
    }

    @Test
    public void toFileFormat_dateOnly_normalisesDate() {
        Deadline deadline = new Deadline("return book", DateTime.parse("2/12/2019"));
        assertEquals("D | 0 | return book | 2019-12-02", deadline.toFileFormat());
    }

    @Test
    public void toFileFormat_doneWithTime_usesOneFlagAndIsoDateTime() {
        Deadline deadline = new Deadline("submit report", DateTime.parse("2019-12-02 1800"));
        deadline.markAsDone();
        assertEquals("D | 1 | submit report | 2019-12-02 1800", deadline.toFileFormat());
    }
}
