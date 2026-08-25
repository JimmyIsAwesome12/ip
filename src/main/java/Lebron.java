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
        String command = scanner.nextLine();
        while (!command.equals("bye")) {
            System.out.println(LINE);
            if (command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                int index = Integer.parseInt(command.substring(5).trim()) - 1;
                tasks[index].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + tasks[index]);
            } else if (command.startsWith("unmark ")) {
                int index = Integer.parseInt(command.substring(7).trim()) - 1;
                tasks[index].markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + tasks[index]);
            } else {
                tasks[taskCount] = new Task(command);
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
