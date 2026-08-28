/**
 * A single to-do item with a description and a done/not-done status.
 */
public class Task {
    /** Separator used between fields when a task is saved to the data file. */
    private static final String FILE_SEPARATOR = " | ";

    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getDescription() {
        return description;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task encoded as one line for the data file, e.g.
     * {@code T | 1 | read book} (type, then 1/0 for done/not done, then the
     * description). Kept deliberately simple and human-readable so the file
     * can be inspected or hand-edited.
     */
    public String toFileFormat() {
        return "T" + FILE_SEPARATOR + (isDone ? "1" : "0") + FILE_SEPARATOR + description;
    }

    /**
     * Reconstructs a task from one line of the data file produced by
     * {@link #toFileFormat()}.
     *
     * @throws IllegalArgumentException if the line is not in the expected
     *     format (wrong number of fields, unknown type, or a done flag that
     *     is not {@code 0} or {@code 1}). Callers use this to detect a
     *     corrupted data file.
     */
    public static Task fromFileFormat(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 3) {
            throw new IllegalArgumentException("expected 3 fields but found " + parts.length);
        }
        String type = parts[0].trim();
        String doneFlag = parts[1].trim();
        String description = parts[2].trim();
        if (!type.equals("T")) {
            throw new IllegalArgumentException("unknown task type '" + type + "'");
        }
        if (description.isEmpty()) {
            throw new IllegalArgumentException("description is empty");
        }
        Task task = new Task(description);
        if (doneFlag.equals("1")) {
            task.markAsDone();
        } else if (!doneFlag.equals("0")) {
            throw new IllegalArgumentException("done flag must be 0 or 1 but was '" + doneFlag + "'");
        }
        return task;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
