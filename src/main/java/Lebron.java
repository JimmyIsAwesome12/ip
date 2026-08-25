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

        String[] tasks = new String[100];
        boolean[] isDone = new boolean[100];
        int taskCount = 0;

        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        while (!command.equals("bye")) {
            System.out.println(LINE);
            if (command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    String mark = isDone[i] ? "X" : " ";
                    System.out.println((i + 1) + ".[" + mark + "] " + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                int index = Integer.parseInt(command.substring(5).trim()) - 1;
                isDone[index] = true;
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  [X] " + tasks[index]);
            } else {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println("added: " + command);
            }
            System.out.println(LINE);
            command = scanner.nextLine();
        }
        scanner.close();

        System.out.println(LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }
}
