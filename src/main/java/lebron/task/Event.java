package lebron.task;

/** A task that spans a period, from a start date/time to an end date/time. */
public class Event extends Task {
    private final DateTime from;
    private final DateTime to;

    public Event(String description, DateTime from, DateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toFileFormat() {
        return "E | " + doneBit() + " | " + description + " | "
                + from.toFileFormat() + " | " + to.toFileFormat();
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
