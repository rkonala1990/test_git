Guide the user through integrating a real LLM call into the `analyze_repo()` function in `main.py`.

Steps to follow:
1. Read `main.py` and show the current `analyze_repo()` function
2. Ask the user which provider they want: Anthropic (Claude), OpenAI, or a custom endpoint
3. Generate a drop-in replacement for `analyze_repo()` that:
   - Calls the chosen LLM API with the `summary` dict serialized as JSON context
   - Uses a clear system prompt explaining the repo analyst role
   - Returns the model's text response
   - Handles API errors gracefully with a fallback to the rule-based logic
4. Show the required pip install command for the chosen SDK
5. Show where to set the API key (environment variable)

Use the Anthropic SDK by default if the user does not specify a provider. Reference the `claude-sonnet-4-6` model for Anthropic calls.
