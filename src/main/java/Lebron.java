import java.util.Scanner;

public class Lebron {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = " _     _____ ____  ____   ___  _   _ \n"
            + "| |   | ____| __ )|  _ \\ / _ \\| \\ | |\n"
            + "| |   |  _| |  _ \\| |_) | | | |  \\| |\n"
            + "| |___| |___| |_) |  _ <| |_| | |\\  |\n"
            + "|_____|_____|____/|_| \\_\\\\___/|_| \\_|\n";

    public static void main(String[] args) {
        System.out.println(LINE);
        System.out.print(BANNER);
        System.out.println("Hello! I'm Lebron.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        Task[] tasks = new Task[100];
        int taskCount = 0;

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
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }
            } else if (keyword.equals("todo")) {
                if (arguments.isEmpty()) {
                    System.out.println("OOPS!!! A todo needs a description, e.g. todo read book");
                } else if (taskCount == tasks.length) {
                    System.out.println("OOPS!!! Your task list is full, I can't add any more.");
                } else {
                    tasks[taskCount] = new Task(arguments);
                    taskCount++;
                    System.out.println("added: " + arguments);
                }
            } else if (keyword.equals("mark") || keyword.equals("unmark")) {
                if (arguments.isEmpty()) {
                    System.out.println("OOPS!!! Tell me which task number to " + keyword
                            + ", e.g. " + keyword + " 2");
                } else {
                    Integer index = null;
                    try {
                        index = Integer.parseInt(arguments);
                    } catch (NumberFormatException e) {
                        System.out.println("OOPS!!! '" + arguments + "' doesn't look like a task number.");
                    }
                    if (index != null && (index < 1 || index > taskCount)) {
                        System.out.println("OOPS!!! There is no task " + index
                                + " in your list. You have " + taskCount + " task(s).");
                    } else if (index != null && keyword.equals("mark")) {
                        tasks[index - 1].markAsDone();
                        System.out.println("Nice! I've marked this task as done:");
                        System.out.println("  " + tasks[index - 1]);
                    } else if (index != null) {
                        tasks[index - 1].markAsNotDone();
                        System.out.println("OK, I've marked this task as not done yet:");
                        System.out.println("  " + tasks[index - 1]);
                    }
                }
            } else {
                System.out.println("OOPS!!! I don't understand that command. "
                        + "Try: list, todo, mark, unmark, or bye.");
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
