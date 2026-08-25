import java.util.ArrayList;
import java.util.Scanner;

public class Lebron {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = " _     _____ ____  ____   ___  _   _ \n"
            + "| |   | ____| __ )|  _ \\ / _ \\| \\ | |\n"
            + "| |   |  _| |  _ \\| |_) | | | |  \\| |\n"
            + "| |___| |___| |_) |  _ <| |_| | |\\  |\n"
            + "|_____|_____|____/|_| \\_\\\\___/|_| \\_|\n";

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

    public static void main(String[] args) {
        System.out.println(LINE);
        System.out.print(BANNER);
        System.out.println("Hello! I'm Lebron.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        ArrayList<Task> tasks = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (!input.trim().equals("bye")) {
            System.out.println(LINE);

            String trimmed = input.trim();
            int spaceIndex = trimmed.indexOf(' ');
            String keyword = spaceIndex == -1 ? trimmed : trimmed.substring(0, spaceIndex);
            String arguments = spaceIndex == -1 ? "" : trimmed.substring(spaceIndex + 1).trim();

            if (keyword.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println((i + 1) + "." + tasks.get(i));
                }
            } else if (keyword.equals("todo")) {
                if (arguments.isEmpty()) {
                    System.out.println("OOPS!!! A todo needs a description, e.g. todo read book");
                } else {
                    tasks.add(new Task(arguments));
                    System.out.println("added: " + arguments);
                }
            } else if (keyword.equals("mark") || keyword.equals("unmark")) {
                Integer index = parseTaskIndex(arguments, keyword, tasks.size());
                if (index != null && keyword.equals("mark")) {
                    tasks.get(index - 1).markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks.get(index - 1));
                } else if (index != null) {
                    tasks.get(index - 1).markAsNotDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks.get(index - 1));
                }
            } else if (keyword.equals("delete")) {
                Integer index = parseTaskIndex(arguments, keyword, tasks.size());
                if (index != null) {
                    Task removed = tasks.remove(index - 1);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + removed);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                }
            } else {
                System.out.println("OOPS!!! I don't understand that command. "
                        + "Try: list, todo, mark, unmark, delete, or bye.");
            }

            System.out.println(LINE);
            input = scanner.nextLine();
        }
        scanner.close();

        System.out.println(LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }
}
