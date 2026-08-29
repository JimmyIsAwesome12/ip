package lebron;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import lebron.exception.LebronException;
import lebron.parser.ParsedCommand;
import lebron.parser.Parser;
import lebron.storage.Storage;
import lebron.task.Task;
import lebron.task.TaskList;
import lebron.task.Todo;
import lebron.ui.Ui;

/**
 * Entry point and top-level control loop. Wires together {@link Ui} (user
 * interaction), {@link Storage} (disk persistence), {@link TaskList} (the
 * task list) and {@link Parser} (understanding commands), then reads and
 * runs commands until the user says {@code bye}.
 */
public class Lebron {
    /** This class only exposes {@link #main(String[])} and is not meant to be instantiated. */
    private Lebron() {
    }

    /**
     * Carries out one parsed command against the task list.
     *
     * @param command the command to run
     * @param tasks the task list to act on
     * @param ui the UI used to show results
     * @param storage the storage to persist changes to
     * @return {@code true} if the program should exit after this command
     * @throws LebronException if the command cannot be completed (e.g. a
     *     task number that is out of range)
     */
    private static boolean runCommand(ParsedCommand command, TaskList tasks, Ui ui, Storage storage)
            throws LebronException {
        switch (command.getType()) {
            case LIST: {
                List<Task> all = tasks.asList();
                ui.showMessage("Here are the tasks in your list:");
                for (int i = 0; i < all.size(); i++) {
                    ui.showMessage((i + 1) + "." + all.get(i));
                }
                return false;
            }
            case TODO:
                tasks.add(new Todo(command.getDescription()));
                storage.save(tasks);
                ui.showMessage("added: " + command.getDescription());
                return false;
            case DEADLINE:
            case EVENT:
                tasks.add(command.getTask());
                storage.save(tasks);
                ui.showMessage("added: " + command.getTask());
                return false;
            case MARK:
            case UNMARK: {
                boolean isMark = command.getType() == ParsedCommand.Type.MARK;
                Task task = isMark ? tasks.mark(command.getIndex()) : tasks.unmark(command.getIndex());
                storage.save(tasks);
                ui.showMessage(isMark
                        ? "Nice! I've marked this task as done:"
                        : "OK, I've marked this task as not done yet:");
                ui.showMessage("  " + task);
                return false;
            }
            case DELETE: {
                Task removed = tasks.delete(command.getIndex());
                storage.save(tasks);
                ui.showMessage("Noted. I've removed this task:");
                ui.showMessage("  " + removed);
                ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
                return false;
            }
            case FIND: {
                List<Task> matches = tasks.find(command.getDescription());
                ui.showMessage("Here are the matching tasks in your list:");
                for (int i = 0; i < matches.size(); i++) {
                    ui.showMessage((i + 1) + "." + matches.get(i));
                }
                return false;
            }
            case BYE:
                ui.showMessage("Bye. Hope to see you again soon!");
                return true;
            default:
                // Parser only ever returns the types handled above.
                throw new LebronException("OOPS!!! I don't understand that command.");
        }
    }

    /**
     * Sets up the UI, storage and task list, then reads and runs commands
     * until the user exits.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        // Tasks are persisted to ./data/lebron.txt (relative to the project
        // root). The path is built from segments so it works on any OS, and
        // Storage handles the file/folder not existing yet.
        Path dataFile = Paths.get("data", "lebron.txt");
        Storage storage = new Storage(dataFile);
        TaskList tasks = new TaskList(storage.load());

        boolean isExit = false;
        while (!isExit) {
            String fullCommand = ui.readCommand();
            ui.showLine();
            try {
                ParsedCommand command = Parser.parse(fullCommand);
                isExit = runCommand(command, tasks, ui, storage);
            } catch (LebronException e) {
                ui.showMessage(e.getMessage());
            }
            ui.showLine();
        }
        ui.close();
    }
}
