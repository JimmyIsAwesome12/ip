import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Lebron {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = " _     _____ ____  ____   ___  _   _ \n"
            + "| |   | ____| __ )|  _ \\ / _ \\| \\ | |\n"
            + "| |   |  _| |  _ \\| |_) | | | |  \\| |\n"
            + "| |___| |___| |_) |  _ <| |_| | |\\  |\n"
            + "|_____|_____|____/|_| \\_\\\\___/|_| \\_|\n";

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
     * Parses a 1-based task index out of {@code arguments}, printing an
     * OOPS!!! error and returning null if it is missing, not a number, or
     * out of range for the given task count.
     */
    private static Integer parseTaskIndex(String arguments, String keyword, int taskCount) {
        if (arguments.isEmpty()) {
            System.out.println("OOPS!!! Tell me which task number to " + keyword
                    + ", e.g. " + keyword + " 2");
            return null;
        }
        int index;
        try {
            index = Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            System.out.println("OOPS!!! '" + arguments + "' doesn't look like a task number.");
            return null;
        }
        if (index < 1 || index > taskCount) {
            System.out.println("OOPS!!! There is no task " + index
                    + " in your list. You have " + taskCount + " task(s).");
            return null;
        }
        return index;
    }

    /**
     * Builds a {@link Deadline} from the text after the {@code deadline}
     * keyword, expected to be {@code <description> /by <date>}. Prints an
     * OOPS!!! message and returns null if the description or date is missing
     * or the date cannot be understood.
     */
    private static Task parseDeadline(String arguments) {
        String usage = "OOPS!!! A deadline needs a description and a /by date, "
                + "e.g. deadline return book /by 2019-12-02 1800";
        int byIndex = arguments.indexOf("/by");
        if (byIndex == -1) {
            System.out.println(usage);
            return null;
        }
        String description = arguments.substring(0, byIndex).trim();
        String by = arguments.substring(byIndex + "/by".length()).trim();
        if (description.isEmpty() || by.isEmpty()) {
            System.out.println(usage);
            return null;
        }
        try {
            return new Deadline(description, DateTime.parse(by));
        } catch (IllegalArgumentException e) {
            System.out.println("OOPS!!! I don't understand the date '" + by
                    + "'. Try e.g. 2019-12-02 or 2019-12-02 1800.");
            return null;
        }
    }

    /**
     * Builds an {@link Event} from the text after the {@code event} keyword,
     * expected to be {@code <description> /from <start> /to <end>}. Prints an
     * OOPS!!! message and returns null if a part is missing or a date cannot
     * be understood.
     */
    private static Task parseEvent(String arguments) {
        String usage = "OOPS!!! An event needs a description, a /from and a /to date, "
                + "e.g. event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600";
        int fromIndex = arguments.indexOf("/from");
        int toIndex = arguments.indexOf("/to");
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            System.out.println(usage);
            return null;
        }
        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = arguments.substring(toIndex + "/to".length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            System.out.println(usage);
            return null;
        }
        try {
            return new Event(description, DateTime.parse(from), DateTime.parse(to));
        } catch (IllegalArgumentException e) {
            System.out.println("OOPS!!! I don't understand one of those dates. "
                    + "Try e.g. 2019-12-02 or 2019-12-02 1800.");
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(LINE);
        System.out.print(BANNER);
        System.out.println("Hello! I'm Lebron.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        // Tasks are persisted to ./data/lebron.txt (relative to the project
        // root). The path is built from segments so it works on any OS, and
        // Storage handles the file/folder not existing yet.
        Path dataFile = Paths.get("data", "lebron.txt");
        Storage storage = new Storage(dataFile);
        ArrayList<Task> tasks = storage.load();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            String trimmed = input.trim();
            int spaceIndex = trimmed.indexOf(' ');
            String keyword = spaceIndex == -1 ? trimmed : trimmed.substring(0, spaceIndex);
            String arguments = spaceIndex == -1 ? "" : trimmed.substring(spaceIndex + 1).trim();
            Command command = Command.fromKeyword(keyword);

            if (command == Command.BYE) {
                break;
            }

            System.out.println(LINE);
            switch (command) {
            case LIST:
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println((i + 1) + "." + tasks.get(i));
                }
                break;
            case TODO:
                if (arguments.isEmpty()) {
                    System.out.println("OOPS!!! A todo needs a description, e.g. todo read book");
                } else {
                    tasks.add(new Todo(arguments));
                    storage.save(tasks);
                    System.out.println("added: " + arguments);
                }
                break;
            case DEADLINE:
            case EVENT: {
                Task task = command == Command.DEADLINE
                        ? parseDeadline(arguments)
                        : parseEvent(arguments);
                if (task != null) {
                    tasks.add(task);
                    storage.save(tasks);
                    System.out.println("added: " + task);
                }
                break;
            }
            case MARK:
            case UNMARK: {
                Integer index = parseTaskIndex(arguments, keyword, tasks.size());
                if (index != null && command == Command.MARK) {
                    tasks.get(index - 1).markAsDone();
                    storage.save(tasks);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks.get(index - 1));
                } else if (index != null) {
                    tasks.get(index - 1).markAsNotDone();
                    storage.save(tasks);
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks.get(index - 1));
                }
                break;
            }
            case DELETE: {
                Integer index = parseTaskIndex(arguments, keyword, tasks.size());
                if (index != null) {
                    Task removed = tasks.remove(index - 1);
                    storage.save(tasks);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + removed);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                }
                break;
            }
            default:
                System.out.println("OOPS!!! I don't understand that command. "
                        + "Try: list, todo, deadline, event, mark, unmark, delete, or bye.");
                break;
            }
            System.out.println(LINE);
        }
        scanner.close();

        System.out.println(LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }
}
