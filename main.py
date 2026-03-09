"""
Sample AI project to connect to a Git repository and analyze its contents.
"""

import os
import sys
from git import Repo, InvalidGitRepositoryError, GitCommandError


def connect_to_repo(repo_path: str) -> Repo:
    """Connect to a local git repository."""
    try:
        repo = Repo(repo_path)
        print(f"Successfully connected to repository at: {repo_path}")
        return repo
    except InvalidGitRepositoryError:
        print(f"Error: '{repo_path}' is not a valid git repository.")
        sys.exit(1)


def get_repo_summary(repo: Repo) -> dict:
    """Collect summary information from the repository."""
    summary = {}

    # Basic info
    summary["active_branch"] = repo.active_branch.name
    summary["remotes"] = [remote.name for remote in repo.remotes]

    # Commit history
    commits = list(repo.iter_commits(max_count=10))
    summary["recent_commits"] = [
        {
            "sha": commit.hexsha[:7],
            "message": commit.message.strip().splitlines()[0],
            "author": str(commit.author),
            "date": commit.committed_datetime.strftime("%Y-%m-%d %H:%M:%S"),
        }
        for commit in commits
    ]

    # File statistics
    tracked_files = [item[0] for item in repo.index.entries.keys()]
    summary["tracked_files_count"] = len(tracked_files)

    # Changed files (unstaged)
    summary["changed_files"] = [item.a_path for item in repo.index.diff(None)]

    # Untracked files
    summary["untracked_files"] = repo.untracked_files

    return summary


def analyze_repo(summary: dict) -> str:
    """
    Produce an AI-style analysis of the repository based on its summary.
    In a real-world scenario this could call an LLM (e.g. OpenAI / Anthropic)
    to generate deeper insights. Here we demonstrate the pattern with a
    rule-based analysis that can easily be swapped for an LLM call.
    """
    lines = ["=== AI Repository Analysis ===", ""]

    lines.append(f"Active branch : {summary['active_branch']}")
    lines.append(
        f"Remotes       : {', '.join(summary['remotes']) if summary['remotes'] else 'none'}"
    )
    lines.append(f"Tracked files : {summary['tracked_files_count']}")
    lines.append("")

    # Recent activity
    commits = summary.get("recent_commits", [])
    if commits:
        lines.append(f"Last {len(commits)} commit(s):")
        for c in commits:
            lines.append(f"  [{c['sha']}] {c['date']}  {c['author']}  —  {c['message']}")
    else:
        lines.append("No commits found.")

    lines.append("")

    # Health observations
    lines.append("--- Observations ---")

    if summary["changed_files"]:
        lines.append(
            f"⚠  {len(summary['changed_files'])} uncommitted change(s) detected: "
            + ", ".join(summary["changed_files"])
        )
    else:
        lines.append("✓  Working tree is clean.")

    if summary["untracked_files"]:
        lines.append(
            f"ℹ  {len(summary['untracked_files'])} untracked file(s): "
            + ", ".join(summary["untracked_files"])
        )

    if len(commits) == 0:
        lines.append("ℹ  Repository has no commits yet.")
    elif len(commits) < 3:
        lines.append("ℹ  Repository is relatively new (fewer than 3 commits).")
    else:
        lines.append("✓  Repository has a healthy commit history.")

    return "\n".join(lines)


def main():
    repo_path = sys.argv[1] if len(sys.argv) > 1 else os.getcwd()

    print(f"Connecting to git repository: {repo_path}\n")

    repo = connect_to_repo(repo_path)
    summary = get_repo_summary(repo)
    analysis = analyze_repo(summary)

    print(analysis)


if __name__ == "__main__":
    main()
