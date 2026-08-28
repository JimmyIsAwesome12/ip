# UI Test Plan

This file lists the UI test cases run by the `test-ui` skill
(`.claude/skills/test-ui`). Each test case is fed to one fresh run of the
program, one command per line, and must end with `bye` so the program
terminates cleanly.

The startup banner and greeting are identical for every run, so **expected
output only needs to cover what the program prints from the first command
onward** (the runner strips the banner/greeting before comparing).

Positive and negative cases are interleaved on purpose, so a bug that
corrupts internal state (e.g. an error path that still increments the task
count) shows up in the very next positive case rather than being masked.

Each test case runs in its own fresh working directory, so tasks the
program saves to `data/lebron.txt` in one test case never carry over to the
next. Two optional blocks support the save/load feature:

- **Data file:** — contents written to `data/lebron.txt` *before* the run,
  for testing that previously saved tasks are loaded on startup.
- **Expected data file:** — contents `data/lebron.txt` must have *after*
  the run, for testing that task-list changes are saved. An empty block
  asserts the file is absent or empty.

When present, these blocks come *after* the Commands and Expected output
blocks.

## Test Case: Greet and exit

- **Aim:** The program greets the user and exits cleanly on `bye` with no
  other input.
- **Commands:**
  ```
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test Case: Add todos and list them

- **Aim:** `todo <description>` adds a task; `list` shows all added tasks,
  unmarked, in order.
- **Commands:**
  ```
  todo read book
  todo return book
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  added: read book
  ____________________________________________________________
  ____________________________________________________________
  added: return book
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[ ] read book
  2.[ ] return book
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test Case: Reject a todo with an empty description

- **Aim:** `todo` with no description is rejected with an error instead of
  adding a blank task, and the session continues normally afterward.
- **Commands:**
  ```
  todo
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  OOPS!!! A todo needs a description, e.g. todo read book
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test Case: Reject an unrecognised command

- **Aim:** Input that isn't a known command keyword is rejected with an
  error instead of silently being treated as a task (this also confirms the
  rejected line was not added as a task -- verified by the next test case
  starting from an empty list).
- **Commands:**
  ```
  blah
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  OOPS!!! I don't understand that command. Try: list, todo, deadline, event, mark, unmark, delete, or bye.
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test Case: Mark a task as done

- **Aim:** `mark <index>` flags the given task done, confirms it, and `list`
  reflects the change while leaving other tasks untouched.
- **Commands:**
  ```
  todo read book
  todo return book
  mark 1
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  added: read book
  ____________________________________________________________
  ____________________________________________________________
  added: return book
  ____________________________________________________________
  ____________________________________________________________
  Nice! I've marked this task as done:
    [X] read book
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[X] read book
  2.[ ] return book
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test Case: Reject mark with a missing task number

- **Aim:** `mark` with no argument is rejected with an error, rather than
  crashing.
- **Commands:**
  ```
  todo read book
  mark
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  added: read book
  ____________________________________________________________
  ____________________________________________________________
  OOPS!!! Tell me which task number to mark, e.g. mark 2
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test Case: Reject mark with a non-numeric task number

- **Aim:** `mark <non-number>` is rejected with an error, rather than
  crashing with a `NumberFormatException`.
- **Commands:**
  ```
  todo read book
  mark two
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  added: read book
  ____________________________________________________________
  ____________________________________________________________
  OOPS!!! 'two' doesn't look like a task number.
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test Case: Reject mark with an out-of-range task number

- **Aim:** `mark <n>` where `n` is outside the current list is rejected with
  an error, rather than crashing with an `ArrayIndexOutOfBoundsException` or
  a null pointer exception.
- **Commands:**
  ```
  todo read book
  mark 5
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  added: read book
  ____________________________________________________________
  ____________________________________________________________
  OOPS!!! There is no task 5 in your list. You have 1 task(s).
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test Case: Unmark a task

- **Aim:** `unmark <index>` reverses a task's done status and confirms it,
  independently of other marked tasks.
- **Commands:**
  ```
  todo read book
  todo return book
  mark 1
  mark 2
  unmark 2
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  added: read book
  ____________________________________________________________
  ____________________________________________________________
  added: return book
  ____________________________________________________________
  ____________________________________________________________
  Nice! I've marked this task as done:
    [X] read book
  ____________________________________________________________
  ____________________________________________________________
  Nice! I've marked this task as done:
    [X] return book
  ____________________________________________________________
  ____________________________________________________________
  OK, I've marked this task as not done yet:
    [ ] return book
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[X] read book
  2.[ ] return book
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test Case: Reject unmark with an out-of-range task number

- **Aim:** `unmark <n>` where `n` is outside the current list is rejected
  with an error, mirroring the same check for `mark`.
- **Commands:**
  ```
  todo read book
  unmark 5
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  added: read book
  ____________________________________________________________
  ____________________________________________________________
  OOPS!!! There is no task 5 in your list. You have 1 task(s).
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test Case: Delete a task from the middle of the list

- **Aim:** `delete <index>` removes the given task, confirms it with the
  removed task's text and the new task count, and `list` shows the
  remaining tasks correctly re-numbered (not just nulled out).
- **Commands:**
  ```
  todo read book
  todo return book
  todo buy bread
  delete 2
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  added: read book
  ____________________________________________________________
  ____________________________________________________________
  added: return book
  ____________________________________________________________
  ____________________________________________________________
  added: buy bread
  ____________________________________________________________
  ____________________________________________________________
  Noted. I've removed this task:
    [ ] return book
  Now you have 2 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[ ] read book
  2.[ ] buy bread
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test Case: Reject delete with a missing task number

- **Aim:** `delete` with no argument is rejected with an error, rather than
  crashing.
- **Commands:**
  ```
  todo read book
  delete
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  added: read book
  ____________________________________________________________
  ____________________________________________________________
  OOPS!!! Tell me which task number to delete, e.g. delete 2
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test Case: Reject delete with an out-of-range task number

- **Aim:** `delete <n>` where `n` is outside the current list is rejected
  with an error, and the task list is left unchanged (verified by `list`
  still showing the one task afterward).
- **Commands:**
  ```
  todo read book
  delete 5
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  added: read book
  ____________________________________________________________
  ____________________________________________________________
  OOPS!!! There is no task 5 in your list. You have 1 task(s).
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[ ] read book
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test Case: Start with no saved data file

- **Aim:** On the very first run (no `data/` folder or data file exists),
  the program starts with an empty task list instead of crashing.
- **Commands:**
  ```
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  Here are the tasks in your list:
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test Case: Load tasks saved from a previous session

- **Aim:** Tasks stored in `data/lebron.txt` are read back on startup,
  preserving their order and done status, so `list` shows them immediately.
- **Commands:**
  ```
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  Here are the tasks in your list:
  1.[ ] read book
  2.[X] return book
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```
- **Data file:**
  ```
  T | 0 | read book
  T | 1 | return book
  ```

## Test Case: Recover from a corrupted data file

- **Aim:** A data file containing a line that is not in the expected format
  loads the valid lines anyway and prints a single warning, rather than
  crashing or discarding everything (stretch goal).
- **Commands:**
  ```
  list
  bye
  ```
- **Expected output:**
  ```
  OOPS!!! Skipped 1 unreadable line(s) in your data file.
  ____________________________________________________________
  Here are the tasks in your list:
  1.[ ] read book
  2.[X] return book
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```
- **Data file:**
  ```
  T | 0 | read book
  this is not a valid line
  T | 1 | return book
  ```

## Test Case: Save the task list after a change

- **Aim:** Adding and then marking a task writes the current list to
  `data/lebron.txt` in the expected format, so the change survives a
  restart.
- **Commands:**
  ```
  todo read book
  mark 1
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  added: read book
  ____________________________________________________________
  ____________________________________________________________
  Nice! I've marked this task as done:
    [X] read book
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```
- **Expected data file:**
  ```
  T | 1 | read book
  ```

## Test Case: Save the task list after a deletion

- **Aim:** Deleting a task rewrites `data/lebron.txt` without the removed
  task, so the deletion is persisted rather than only applied in memory.
- **Commands:**
  ```
  delete 1
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  Noted. I've removed this task:
    [ ] read book
  Now you have 1 tasks in the list.
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```
- **Data file:**
  ```
  T | 0 | read book
  T | 1 | return book
  ```
- **Expected data file:**
  ```
  T | 1 | return book
  ```

## Test Case: Add a deadline with a date only

- **Aim:** `deadline <desc> /by <yyyy-mm-dd>` parses the date and prints it
  back in `MMM dd yyyy` form (not as the raw input string).
- **Commands:**
  ```
  deadline return book /by 2019-12-02
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  added: [D][ ] return book (by: Dec 02 2019)
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[D][ ] return book (by: Dec 02 2019)
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```
- **Expected data file:**
  ```
  D | 0 | return book | 2019-12-02
  ```

## Test Case: Add a deadline with a date and time

- **Aim:** A `HHmm` time after the date is parsed and shown as a 12-hour
  clock time, e.g. `1800` becomes `6:00pm`.
- **Commands:**
  ```
  deadline submit report /by 2019-12-02 1800
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  added: [D][ ] submit report (by: Dec 02 2019 6:00pm)
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```
- **Expected data file:**
  ```
  D | 0 | submit report | 2019-12-02 1800
  ```

## Test Case: Accept the day-first date format from the requirement

- **Aim:** The `d/M/yyyy HHmm` form (`2/12/2019 1800` = 2 Dec 2019, 6pm)
  from the requirement's example is understood and normalised.
- **Commands:**
  ```
  deadline pay bill /by 2/12/2019 1800
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  added: [D][ ] pay bill (by: Dec 02 2019 6:00pm)
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```
- **Expected data file:**
  ```
  D | 0 | pay bill | 2019-12-02 1800
  ```

## Test Case: Add an event with start and end date-times

- **Aim:** `event <desc> /from <date> /to <date>` parses both endpoints.
- **Commands:**
  ```
  event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  added: [E][ ] project meeting (from: Dec 02 2019 2:00pm to: Dec 02 2019 4:00pm)
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[E][ ] project meeting (from: Dec 02 2019 2:00pm to: Dec 02 2019 4:00pm)
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```
- **Expected data file:**
  ```
  E | 0 | project meeting | 2019-12-02 1400 | 2019-12-02 1600
  ```

## Test Case: Reject a deadline with a missing /by

- **Aim:** `deadline` without a `/by` section is rejected with a usage hint
  rather than being stored with no date.
- **Commands:**
  ```
  deadline return book
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  OOPS!!! A deadline needs a description and a /by date, e.g. deadline return book /by 2019-12-02 1800
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test Case: Reject a deadline with an unparseable date

- **Aim:** A `/by` value that matches none of the accepted date formats is
  rejected with an error instead of crashing or storing a bad task.
- **Commands:**
  ```
  deadline return book /by someday
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  OOPS!!! I don't understand the date 'someday'. Try e.g. 2019-12-02 or 2019-12-02 1800.
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test Case: Load deadlines and events saved from a previous session

- **Aim:** `D` and `E` lines in the data file are read back into the right
  task types, with their dates re-formatted for display and their done
  status preserved.
- **Commands:**
  ```
  list
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  Here are the tasks in your list:
  1.[D][X] return book (by: Dec 02 2019 6:00pm)
  2.[E][ ] camp (from: Jun 01 2019 to: Jun 03 2019)
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```
- **Data file:**
  ```
  D | 1 | return book | 2019-12-02 1800
  E | 0 | camp | 2019-06-01 | 2019-06-03
  ```

## Test Case: Skip a data-file line with a corrupted date

- **Aim:** A `D` line whose date field is not a valid date is treated as
  corruption -- skipped with a warning -- while the other tasks still load
  (stretch goal).
- **Commands:**
  ```
  list
  bye
  ```
- **Expected output:**
  ```
  OOPS!!! Skipped 1 unreadable line(s) in your data file.
  ____________________________________________________________
  Here are the tasks in your list:
  1.[D][ ] return book (by: Dec 02 2019)
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```
- **Data file:**
  ```
  D | 0 | return book | 2019-12-02
  D | 0 | pay rent | last tuesday
  ```
