Set up the project environment from scratch.

Steps:
1. Check Python version with `python3 --version`
2. Check if `requirements.txt` exists; if not, report it is missing
3. Run `pip install -r requirements.txt` to install dependencies
4. Verify GitPython installed: `python3 -c "import git; print(git.__version__)"`
5. Run a quick smoke test: `python3 main.py` (if `main.py` exists)

Report the result of each step clearly. If any step fails, show the error and suggest a fix.
