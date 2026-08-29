package lebron.task;

/** A task that must be completed by a given date (and optionally time). */
public class Deadline extends Task {
    private final DateTime by;

    /**
     * Creates a deadline task.
     *
     * @param description what needs to be done
     * @param by when it is due
     */
    public Deadline(String description, DateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toFileFormat() {
        return "D | " + doneBit() + " | " + description + " | " + by.toFileFormat();
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
