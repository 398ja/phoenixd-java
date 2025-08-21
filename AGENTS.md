# Repo Guidelines

## Description
A simple Java client for ACINQ's [phoenixd REST API](https://phoenix.acinq.co/server/api). It wraps the HTTP endpoints and exposes typed requests and responses.


## Testing

- Always run `mvn -q verify` from the repository root before committing your changes.
- Include the command's output in the PR description.
- If tests fail due to dependency or network issues, mention this in the PR.
- Update the `README.md` file if you add or modify features.
- Update the `pom.xml` file for new modules or dependencies, ensuring compatibility with Java 21.
- Verify new Dockerfiles or `docker-compose.yml` files by running `docker-compose build`.
- Document new REST endpoints in the API documentation and ensure they are tested.
- Add unit tests for new functionality, covering edge cases.
- Ensure modifications to existing code do not break functionality and pass all tests.
- Add integration tests for new features to verify end-to-end functionality.
- Ensure new dependencies or configurations do not introduce security vulnerabilities.
- Maintain the versions in the configuration section of the pom.xml files.

## Pull Requests

- Summarize the changes made and describe how they were tested.
- Include any limitations or known issues in the description.
- Add a "Network Access" section summarizing blocked domains if network requests were denied.
- Ensure all new features, modules, or dependencies are properly documented in the `README.md` file.
- Always follow the repository's PR submission guidelines and use the PR template located at `.github/pull_request_template.md`.

## PR Quality Gate

- PR summaries must reference modified files with file path citations (e.g. `F:path/to/file.java†L1-L2`).
- PR titles and commit messages must follow the `type: description` naming format.
- Allowed types: feat, fix, docs, refactor, test, chore, ci, build, perf, style.
- The description should be a concise verb + object phrase (e.g., `refactor: Refactor auth middleware to async`).
- Include a Testing section listing the commands run. Prefix each command with ✅, ⚠️, or ❌ and cite relevant terminal output.
- If network requests fail, add a Network Access section noting blocked domains.
- When TODOs or placeholders remain, include a Notes section.
- Review AI-generated changes with developer expertise, ensuring you understand why the code works and that it remains resilient, scalable, and secure.
- Use `rg` for search instead of `ls -R` or `grep -R`.
- Ensure all new features are compliant with the API specification provided above.
