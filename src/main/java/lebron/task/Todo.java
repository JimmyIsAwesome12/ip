package lebron.task;

/** A task with only a description and no associated date. */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     *
     * @param description what needs to be done
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toFileFormat() {
        return "T | " + doneBit() + " | " + description;
    }
}
