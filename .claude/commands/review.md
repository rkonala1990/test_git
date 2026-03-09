Review all staged and unstaged changes in the repository and provide structured feedback.

Steps:
1. Run `git diff HEAD` to see all changes
2. Run `git status` for the overall working tree state
3. Analyze the diff and provide feedback in this format:

**Summary** — one sentence describing what changed

**Issues** — list any bugs, logic errors, or security concerns (or "None found")

**Suggestions** — up to 3 concrete improvement ideas

**Verdict** — Ready to commit / Needs changes

Keep feedback concise and actionable. Focus on correctness and clarity over style.
