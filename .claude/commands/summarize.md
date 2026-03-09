Summarize the recent git history of this repository in a human-readable format.

Steps:
1. Run `git log --oneline -20` to get the last 20 commits
2. Run `git shortlog -sn --no-merges` for contributor stats
3. Run `git diff --stat HEAD~5 HEAD` for recent file activity (skip if fewer than 5 commits)

Present the results as:

**Recent Activity (last 20 commits)**
- Bullet list of commits grouped by theme/area if possible

**Contributors**
- Table of author → commit count

**Most Changed Files (last 5 commits)**
- List of files with change frequency

End with a one-paragraph narrative summary of what the project has been focused on recently.
