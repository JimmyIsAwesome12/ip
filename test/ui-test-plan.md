# UI Test Plan

This file lists the UI test cases run by the `test-ui` skill
(`.claude/skills/test-ui`). Each test case is fed to one fresh run of the
program, one command per line, and must end with `bye` so the program
terminates cleanly.

The startup banner and greeting are identical for every run, so **expected
output only needs to cover what the program prints from the first command
onward** (the runner strips the banner/greeting before comparing).

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

## Test Case: Echo an unrecognised command

- **Aim:** Text that isn't a known command is stored and echoed back with an
  `added:` prefix.
- **Commands:**
  ```
  blah
  bye
  ```
- **Expected output:**
  ```
  ____________________________________________________________
  added: blah
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

## Test Case: Add tasks and list them

- **Aim:** Tasks added one at a time are stored in order and shown, unmarked,
  by `list`.
- **Commands:**
  ```
  read book
  return book
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

## Test Case: Mark a task as done

- **Aim:** `mark <index>` flags the given task done, confirms it, and `list`
  reflects the change while leaving other tasks untouched.
- **Commands:**
  ```
  read book
  return book
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

## Test Case: Unmark a task

- **Aim:** `unmark <index>` reverses a task's done status and confirms it,
  independently of other marked tasks.
- **Commands:**
  ```
  read book
  return book
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
