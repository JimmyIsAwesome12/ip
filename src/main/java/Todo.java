/** A task with only a description and no associated date. */
public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toFileFormat() {
        return "T | " + doneBit() + " | " + description;
    }
}
