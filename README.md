# TodoTxtFX

[![build](https://github.com/phybros/TodoTxtFX/actions/workflows/build.yml/badge.svg)](https://github.com/phybros/TodoTxtFX/actions/workflows/build.yml) [![GitHub release](https://img.shields.io/github/release/phybros/TodoTxtFX.svg)](https://GitHub.com/phybros/TodoTxtFX/releases/)

TodoTxtFX is a macOS and Windows client for the [todo.txt](https://github.com/todotxt/todo.txt) format.

## Installing

The latest build is always available at [https://github.com/phybros/TodoTxtFX/releases/latest](https://github.com/phybros/TodoTxtFX/releases/latest). Go there and download the right client for your OS (`.msi` for Windows, `.pkg` for macOS).

The builds are not codesigned, so you will need to "Open Anyway" via the Security & Privacy settings screen on macOS, and on Windows when Smart Screen pops up, you can click More Info -> Run Anyway.

## Using

When the app starts it will ask you to choose a `todo.txt` file.

Once chosen you can change to a different file at any time with the hotkey ⌘O (Ctrl-O on Windows).

To save your todo.txt file, use ⌘S (Ctrl-S on Windows).

### Hotkeys

* ⌘O open a todo.txt file
* ⌘S save the currently open file
* ⌘N focus the new task box
* X complete task
* ⌘↑ increase priority
* ⌘↓ decrease priority
* ⌘← remove priority

> Note: use Ctrl in place of ⌘ on Windows.

## Features

* Automatically reloads when your todo.txt file is change by another app
* Keeps tasks sorted by `completed`, `priority`, and then `name` in that order (this could be considered a bug)
* Highlights `@contexts` and `+projects`
