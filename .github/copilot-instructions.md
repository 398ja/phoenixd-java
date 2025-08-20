# PR Review Instructions

Provide a concise summary of the pull request and highlight any risks or follow-up work. When tests are mentioned, state whether they passed or failed. Organize the output using the following sections:

- Summary
- Testing
- Review Notes
- 
# Reviewer Guidance for GitHub Copilot

- Ensure pull request titles and commit messages follow `type: Description` using a verb and an object (e.g., `fix: handle null node`).
- Verify the description explains the rationale for the change and links to related issues when available.
- Flag any breaking changes clearly with **BREAKING** and confirm the risk is documented.
- Confirm tests cover new or modified code paths; suggest additions where coverage is missing.
- Provide targeted feedback and call out areas where further clarification or review focus is needed.
