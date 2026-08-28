/**
 * Turns a raw line typed by the user into a {@link ParsedCommand}.
 *
 * <p>All the "does this make sense?" checks live here: unknown keywords,
 * missing descriptions, non-numeric task numbers, and unparseable dates are
 * all reported by throwing {@link LebronException} with a message meant to
 * be shown to the user.
 */
public class Parser {
    /**
     * Parses one full command line.
     *
     * @throws LebronException if the line is not a command the chatbot
     *     understands, or a required part is missing or malformed
     */
    public static ParsedCommand parse(String fullCommand) throws LebronException {
        String trimmed = fullCommand.trim();
        int spaceIndex = trimmed.indexOf(' ');
        String keyword = spaceIndex == -1 ? trimmed : trimmed.substring(0, spaceIndex);
        String arguments = spaceIndex == -1 ? "" : trimmed.substring(spaceIndex + 1).trim();

        switch (keyword) {
        case "list":
            return ParsedCommand.list();
        case "bye":
            return ParsedCommand.bye();
        case "todo":
            if (arguments.isEmpty()) {
                throw new LebronException("OOPS!!! A todo needs a description, e.g. todo read book");
            }
            return ParsedCommand.todo(arguments);
        case "deadline":
            return ParsedCommand.ofTask(ParsedCommand.Type.DEADLINE, parseDeadline(arguments));
        case "event":
            return ParsedCommand.ofTask(ParsedCommand.Type.EVENT, parseEvent(arguments));
        case "mark":
            return ParsedCommand.ofIndex(ParsedCommand.Type.MARK, parseTaskIndex(arguments, "mark"));
        case "unmark":
            return ParsedCommand.ofIndex(ParsedCommand.Type.UNMARK, parseTaskIndex(arguments, "unmark"));
        case "delete":
            return ParsedCommand.ofIndex(ParsedCommand.Type.DELETE, parseTaskIndex(arguments, "delete"));
        default:
            throw new LebronException("OOPS!!! I don't understand that command. "
                    + "Try: list, todo, deadline, event, mark, unmark, delete, or bye.");
        }
    }

    /**
     * Parses a 1-based task index out of {@code arguments}.
     *
     * @throws LebronException if it is missing or not a number (range
     *     checking is left to {@link TaskList})
     */
    private static int parseTaskIndex(String arguments, String keyword) throws LebronException {
        if (arguments.isEmpty()) {
            throw new LebronException("OOPS!!! Tell me which task number to " + keyword
                    + ", e.g. " + keyword + " 2");
        }
        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new LebronException("OOPS!!! '" + arguments + "' doesn't look like a task number.");
        }
    }

    /**
     * Builds a {@link Deadline} from {@code <description> /by <date>}.
     *
     * @throws LebronException if the description or date is missing or the
     *     date cannot be understood
     */
    private static Task parseDeadline(String arguments) throws LebronException {
        String usage = "OOPS!!! A deadline needs a description and a /by date, "
                + "e.g. deadline return book /by 2019-12-02 1800";
        int byIndex = arguments.indexOf("/by");
        if (byIndex == -1) {
            throw new LebronException(usage);
        }
        String description = arguments.substring(0, byIndex).trim();
        String by = arguments.substring(byIndex + "/by".length()).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new LebronException(usage);
        }
        try {
            return new Deadline(description, DateTime.parse(by));
        } catch (IllegalArgumentException e) {
            throw new LebronException("OOPS!!! I don't understand the date '" + by
                    + "'. Try e.g. 2019-12-02 or 2019-12-02 1800.");
        }
    }

    /**
     * Builds an {@link Event} from {@code <description> /from <start> /to <end>}.
     *
     * @throws LebronException if a part is missing or a date cannot be
     *     understood
     */
    private static Task parseEvent(String arguments) throws LebronException {
        String usage = "OOPS!!! An event needs a description, a /from and a /to date, "
                + "e.g. event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600";
        int fromIndex = arguments.indexOf("/from");
        int toIndex = arguments.indexOf("/to");
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new LebronException(usage);
        }
        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = arguments.substring(toIndex + "/to".length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new LebronException(usage);
        }
        try {
            return new Event(description, DateTime.parse(from), DateTime.parse(to));
        } catch (IllegalArgumentException e) {
            throw new LebronException("OOPS!!! I don't understand one of those dates. "
                    + "Try e.g. 2019-12-02 or 2019-12-02 1800.");
        }
    }
}
