package lebron.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lebron.task.Task;
import lebron.task.TaskList;

/**
 * Loads the task list from, and saves it to, a plain-text file on disk so
 * that tasks survive between runs of the program.
 *
 * <p>The file holds one task per line in the format produced by
 * {@link Task#toFileFormat()}, e.g. {@code T | 1 | read book}. The location
 * is passed in as a {@link Path} (built from path segments rather than a
 * literal string) so it stays relative to the project root and works the
 * same on every operating system.
 */
public class Storage {
    private final Path file;

    /**
     * Creates a storage bound to the given data file.
     *
     * @param file the path tasks are read from and written to
     */
    public Storage(Path file) {
        this.file = file;
    }

    /**
     * Reads the saved tasks from disk.
     *
     * <p>Returns an empty list if the data file does not exist yet -- the
     * normal situation the first time the program is run on a new computer,
     * or before anything has ever been saved. Lines that are not in the
     * expected format are skipped rather than aborting the load, and a
     * single summary warning is printed, so a partially corrupted file
     * still recovers as many tasks as possible.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(file)) {
            return tasks;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            System.out.println("OOPS!!! I couldn't read your saved tasks (" + e.getMessage()
                    + "). Starting with an empty list.");
            return tasks;
        }

        int skipped = 0;
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(Task.fromFileFormat(line));
            } catch (IllegalArgumentException e) {
                skipped++;
            }
        }
        if (skipped > 0) {
            System.out.println("OOPS!!! Skipped " + skipped
                    + " unreadable line(s) in your data file.");
        }
        return tasks;
    }

    /**
     * Writes the given tasks to disk, replacing any previous contents.
     *
     * <p>Creates the containing folder (e.g. {@code ./data/}) first if it
     * does not exist yet. Any I/O failure is reported to the user but does
     * not crash the program, so a save problem never loses the in-memory
     * list mid-session.
     */
    public void save(TaskList tasks) {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks.asList()) {
            lines.add(task.toFileFormat());
        }
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(file, lines);
        } catch (IOException e) {
            System.out.println("OOPS!!! I couldn't save your tasks (" + e.getMessage() + ").");
        }
    }
}
