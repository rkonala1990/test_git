# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A sample Python project demonstrating programmatic Git repository analysis, designed with LLM integration in mind. Uses GitPython to collect repository metadata and produces structured analysis output.

## Commands

```bash
# Install dependencies
pip install -r requirements.txt

# Run against current directory (must be inside a git repo)
python main.py

# Run against a specific repository path
python main.py /path/to/repo
```

No build, lint, or test tooling is configured.

## Architecture

The project is a single file (`main.py`) with a linear pipeline:

```
CLI args → connect_to_repo() → get_repo_summary() → analyze_repo() → stdout
```

- **`connect_to_repo(repo_path)`** — Opens a GitPython `Repo` object; exits with error message on invalid path.
- **`get_repo_summary(repo)`** — Returns a dict with branch name, remotes, last 10 commits, file counts, and working tree status.
- **`analyze_repo(summary)`** — Produces formatted text analysis using rule-based logic. This function is explicitly designed as a drop-in replacement target for LLM calls (e.g., Anthropic/OpenAI API).

## Extension Point

The `analyze_repo()` function is the intended integration point for swapping in a real LLM call. The `summary` dict it receives is the structured data to pass as context to the model.
