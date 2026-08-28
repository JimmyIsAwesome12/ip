import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Lebron {
    private enum Command {
        LIST, TODO, DEADLINE, EVENT, MARK, UNMARK, DELETE, BYE, UNKNOWN;

        static Command fromKeyword(String keyword) {
            switch (keyword) {
            case "list":
                return LIST;
            case "todo":
                return TODO;
            case "deadline":
                return DEADLINE;
            case "event":
                return EVENT;
            case "mark":
                return MARK;
            case "unmark":
                return UNMARK;
            case "delete":
                return DELETE;
            case "bye":
                return BYE;
            default:
                return UNKNOWN;
            }
        }
    }

    /**
     * Parses a 1-based task index out of {@code arguments}, showing an
     * OOPS!!! error and returning null if it is missing or not a number.
     * Range checking is done later by {@link TaskList}.
     */
    private static Integer parseTaskIndex(String arguments, String keyword, Ui ui) {
        if (arguments.isEmpty()) {
            ui.showMessage("OOPS!!! Tell me which task number to " + keyword
                    + ", e.g. " + keyword + " 2");
            return null;
        }
        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            ui.showMessage("OOPS!!! '" + arguments + "' doesn't look like a task number.");
            return null;
        }
    }

    /**
     * Builds a {@link Deadline} from the text after the {@code deadline}
     * keyword, expected to be {@code <description> /by <date>}. Shows an
     * OOPS!!! message and returns null if the description or date is missing
     * or the date cannot be understood.
     */
    private static Task parseDeadline(String arguments, Ui ui) {
        String usage = "OOPS!!! A deadline needs a description and a /by date, "
                + "e.g. deadline return book /by 2019-12-02 1800";
        int byIndex = arguments.indexOf("/by");
        if (byIndex == -1) {
            ui.showMessage(usage);
            return null;
        }
        String description = arguments.substring(0, byIndex).trim();
        String by = arguments.substring(byIndex + "/by".length()).trim();
        if (description.isEmpty() || by.isEmpty()) {
            ui.showMessage(usage);
            return null;
        }
        try {
            return new Deadline(description, DateTime.parse(by));
        } catch (IllegalArgumentException e) {
            ui.showMessage("OOPS!!! I don't understand the date '" + by
                    + "'. Try e.g. 2019-12-02 or 2019-12-02 1800.");
            return null;
        }
    }

    /**
     * Builds an {@link Event} from the text after the {@code event} keyword,
     * expected to be {@code <description> /from <start> /to <end>}. Shows an
     * OOPS!!! message and returns null if a part is missing or a date cannot
     * be understood.
     */
    private static Task parseEvent(String arguments, Ui ui) {
        String usage = "OOPS!!! An event needs a description, a /from and a /to date, "
                + "e.g. event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600";
        int fromIndex = arguments.indexOf("/from");
        int toIndex = arguments.indexOf("/to");
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            ui.showMessage(usage);
            return null;
        }
        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = arguments.substring(toIndex + "/to".length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            ui.showMessage(usage);
            return null;
        }
        try {
            return new Event(description, DateTime.parse(from), DateTime.parse(to));
        } catch (IllegalArgumentException e) {
            ui.showMessage("OOPS!!! I don't understand one of those dates. "
                    + "Try e.g. 2019-12-02 or 2019-12-02 1800.");
            return null;
        }
    }

    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        // Tasks are persisted to ./data/lebron.txt (relative to the project
        // root). The path is built from segments so it works on any OS, and
        // Storage handles the file/folder not existing yet.
        Path dataFile = Paths.get("data", "lebron.txt");
        Storage storage = new Storage(dataFile);
        TaskList tasks = new TaskList(storage.load());

        while (true) {
            String input = ui.readCommand();
            String trimmed = input.trim();
            int spaceIndex = trimmed.indexOf(' ');
            String keyword = spaceIndex == -1 ? trimmed : trimmed.substring(0, spaceIndex);
            String arguments = spaceIndex == -1 ? "" : trimmed.substring(spaceIndex + 1).trim();
            Command command = Command.fromKeyword(keyword);

            if (command == Command.BYE) {
                break;
            }

            ui.showLine();
            try {
                switch (command) {
                case LIST: {
                    List<Task> all = tasks.asList();
                    ui.showMessage("Here are the tasks in your list:");
                    for (int i = 0; i < all.size(); i++) {
                        ui.showMessage((i + 1) + "." + all.get(i));
                    }
                    break;
                }
                case TODO:
                    if (arguments.isEmpty()) {
                        ui.showMessage("OOPS!!! A todo needs a description, e.g. todo read book");
                    } else {
                        tasks.add(new Todo(arguments));
                        storage.save(tasks);
                        ui.showMessage("added: " + arguments);
                    }
                    break;
                case DEADLINE:
                case EVENT: {
                    Task task = command == Command.DEADLINE
                            ? parseDeadline(arguments, ui)
                            : parseEvent(arguments, ui);
                    if (task != null) {
                        tasks.add(task);
                        storage.save(tasks);
                        ui.showMessage("added: " + task);
                    }
                    break;
                }
                case MARK:
                case UNMARK: {
                    Integer index = parseTaskIndex(arguments, keyword, ui);
                    if (index != null) {
                        Task task = command == Command.MARK
                                ? tasks.mark(index)
                                : tasks.unmark(index);
                        storage.save(tasks);
                        ui.showMessage(command == Command.MARK
                                ? "Nice! I've marked this task as done:"
                                : "OK, I've marked this task as not done yet:");
                        ui.showMessage("  " + task);
                    }
                    break;
                }
                case DELETE: {
                    Integer index = parseTaskIndex(arguments, keyword, ui);
                    if (index != null) {
                        Task removed = tasks.delete(index);
                        storage.save(tasks);
                        ui.showMessage("Noted. I've removed this task:");
                        ui.showMessage("  " + removed);
                        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
                    }
                    break;
                }
                default:
                    ui.showMessage("OOPS!!! I don't understand that command. "
                            + "Try: list, todo, deadline, event, mark, unmark, delete, or bye.");
                    break;
                }
            } catch (LebronException e) {
                ui.showMessage(e.getMessage());
            }
            ui.showLine();
        }
        ui.close();
        ui.showGoodbye();
    }
}
