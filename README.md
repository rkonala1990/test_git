# test_git

A sample AI project that connects to a Git repository and produces an
AI-style analysis of its contents (branch, commit history, working-tree
health, and more).

## Requirements

- Python 3.8+
- [GitPython](https://gitpython.readthedocs.io/)

## Installation

```bash
pip install -r requirements.txt
```

## Usage

Analyse the current directory (must be inside a git repo):

```bash
python main.py
```

Analyse a specific repository path:

```bash
python main.py /path/to/your/repo
```

## Example output

```
Connecting to git repository: /path/to/your/repo

Successfully connected to repository at: /path/to/your/repo

=== AI Repository Analysis ===

Active branch : main
Remotes       : origin
Tracked files : 4

Last 5 commit(s):
  [98e8f85] 2026-03-09 10:00:00  Jane Doe  —  Add README
  ...

--- Observations ---
✓  Working tree is clean.
✓  Repository has a healthy commit history.
```

