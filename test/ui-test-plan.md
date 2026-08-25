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

- **Aim:** Input that isn't `list`, `todo`, `mark`, `unmark`, or `bye` is
  rejected with an error instead of silently being treated as a task (this
  also confirms the rejected line was not added as a task -- verified by the
  next test case starting from an empty list).
- **Commands:**
  ```
  blah
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  OOPS!!! I don't understand that command. Try: list, todo, mark, unmark, or bye.
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
