# Repo Guidelines

## Description
A simple Java client for ACINQ's [phoenixd REST API](https://phoenix.acinq.co/server/api). It wraps the HTTP endpoints and exposes typed requests and responses.

## Project
- Maintain the versions in the configuration section of the parent pom.xml file.

## Coding

When writing code, follow Clean Code and Clean Architecture principles:

### Meaningful Names
- Use intention-revealing names that explain why something exists, what it does, and how it's used
- Avoid disinformation: don't use names that could mislead (e.g., `accountList` for something that isn't a List)
- Make meaningful distinctions: avoid noise words like `Info`, `Data`, `Manager` unless they add real meaning
- Use pronounceable and searchable names; avoid single-letter variables except for loop counters
- Class names should be nouns (`Customer`, `Account`); method names should be verbs (`postPayment`, `save`)
- Pick one word per concept and stick with it (`fetch`, `retrieve`, `get` — pick one)

### Functions
- Keep functions small: ideally under 20 lines, rarely exceeding one screen
- Functions should do one thing, do it well, and do it only
- One level of abstraction per function: don't mix high-level policy with low-level details
- Use descriptive names: a long descriptive name is better than a short cryptic one
- Minimize arguments: zero is ideal, one or two are fine, three requires justification
- Avoid flag arguments (boolean parameters that change behavior)
- Functions should either do something or answer something, never both (Command-Query Separation)
- Prefer exceptions over error codes; extract try/catch blocks into their own functions

### Comments
- Comments are a failure to express intent in code; prefer self-documenting code
- Good comments: legal comments, explanation of intent, clarification, warning of consequences, TODO notes, Javadoc for public APIs
- Bad comments: redundant comments, misleading comments, mandated comments, journal comments, noise comments, commented-out code
- If you must comment, explain *why*, not *what* — the code shows what

### Error Handling
- Use exceptions rather than return codes
- Write try-catch-finally statements first when writing code that could throw
- Use unchecked exceptions; checked exceptions violate the Open/Closed Principle
- Provide context with exceptions: include operation attempted and failure type
- Define exception classes by how they're caught, not by their source
- Don't return null — throw an exception or return a Special Case object instead
- Don't pass null as arguments unless the API explicitly expects it

### Classes
- Classes should be small, measured by responsibilities (Single Responsibility Principle)
- A class should have only one reason to change
- High cohesion: methods and variables should be closely related
- Organize for change: isolate code that's likely to change from code that's stable
- Depend on abstractions, not concretions (Dependency Inversion)
- Classes should be open for extension but closed for modification (Open/Closed Principle)

### Code Smells and Heuristics
- Avoid comments that could be replaced by better naming or structure
- Eliminate dead code, duplicate code, and code at wrong levels of abstraction
- Keep configuration data at high levels; don't bury magic numbers
- Follow the Law of Demeter: modules shouldn't know about the innards of objects they manipulate
- Make logical dependencies physical: if one module depends on another, make that explicit
- Prefer polymorphism to if/else or switch/case chains
- Follow standard conventions for the project and language
- Replace magic numbers with named constants
- Be precise: don't be lazy about decisions — if you decide to use a list, be sure you need one
- Encapsulate conditionals: extract complex boolean expressions into well-named methods
- Avoid negative conditionals: `if (buffer.shouldCompact())` is clearer than `if (!buffer.shouldNotCompact())`
- Functions should descend one level of abstraction

### SOLID Design Principles

**Single Responsibility Principle (SRP)**
A module should have one, and only one, reason to change. Each class serves one actor or stakeholder. When requirements change for one actor, only the relevant module changes.

**Open/Closed Principle (OCP)**
Software entities should be open for extension but closed for modification. Achieve this through abstraction and polymorphism — add new behavior by adding new code, not changing existing code.

**Liskov Substitution Principle (LSP)**
Subtypes must be substitutable for their base types without altering program correctness. If S is a subtype of T, objects of type T may be replaced with objects of type S without breaking the program.

**Interface Segregation Principle (ISP)**
Clients should not be forced to depend on interfaces they don't use. Prefer many small, client-specific interfaces over one general-purpose interface.

**Dependency Inversion Principle (DIP)**
High-level modules should not depend on low-level modules; both should depend on abstractions. Abstractions should not depend on details; details should depend on abstractions.

### Component Principles

**Cohesion Principles:**
- **REP (Reuse/Release Equivalence)**: The granule of reuse is the granule of release — classes in a component should be releasable together
- **CCP (Common Closure)**: Gather classes that change for the same reasons at the same times; separate classes that change at different times for different reasons
- **CRP (Common Reuse)**: Don't force users to depend on things they don't need — classes in a component should be used together

**Coupling Principles:**
- **ADP (Acyclic Dependencies)**: No cycles in the component dependency graph; use Dependency Inversion to break cycles
- **SDP (Stable Dependencies)**: Depend in the direction of stability — volatile components should depend on stable ones
- **SAP (Stable Abstractions)**: Stable components should be abstract; instability should be concrete

### Design Patterns
Apply established patterns where appropriate:
- **Creational**: Factory Method, Abstract Factory, Builder, Singleton (use sparingly), Prototype
- **Structural**: Adapter, Bridge, Composite, Decorator, Facade, Proxy
- **Behavioral**: Strategy, Observer, Command, State, Template Method, Iterator, Chain of Responsibility

Choose patterns that simplify the design; don't force patterns where simpler solutions suffice.

### General Guidelines
- When committing code, follow the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification
- Make use of the Lombok library to reduce boilerplate code
- Always rely on imports rather than fully qualified class names in code to keep implementations readable

## Documentation

- When generating documentation:
    - Follow the Diátaxis framework and classify each document as a tutorial, how-to guide, reference, or explanation.
    - Place new Markdown files under `docs/<section>` matching the chosen category.
    - Start each document with a top-level `#` heading and a short introduction that states the purpose.
    - Link the document from `docs/README.md` in the corresponding section.
    - Use relative links to reference other documents and keep code snippets minimal and tested.
    - Consult the following resources on Diátaxis for guidance:
        - https://github.blog/developer-skills/documentation-done-right-a-developers-guide/
        - https://diataxis.fr/
        - https://diataxis.fr/start-here/
        - https://diataxis.fr/how-to-use-diataxis/
        - https://diataxis.fr/tutorials/
        - https://diataxis.fr/how-to-guides/
        - https://diataxis.fr/tutorials-how-to/
        - https://diataxis.fr/quality/
        - https://diataxis.fr/complex-hierarchies/
        - https://diataxis.fr/compass/

## Testing

- Always run `mvn -q verify` from the repository root before committing your changes.
- Include the command's output in the PR description.
- If tests fail due to dependency or network issues, mention this in the PR.
- Update the documentation files if you add or modify features.
- Update the `pom.xml` file for new modules or dependencies, ensuring compatibility with Java 21.
- Verify new Dockerfiles or `docker-compose.yml` files by running `docker-compose build`.
- Document new REST endpoints in the API documentation and ensure they are tested.
- Add unit tests for new functionality, covering edge cases. Follow "Clean Code" principles on unit tests, as described in the "Clean Code" book (Chapter 9).
- Ensure modifications to existing code do not break functionality and pass all tests.
- Add integration tests for new features to verify end-to-end functionality.
- Ensure new dependencies or configurations do not introduce security vulnerabilities.
- Add a comment on top of every test method to describe the test in plain English.

## Pull Requests

- Always follow the repository's PR submission guidelines and use the PR template located at `.github/pull_request_template.md`.
- Summarize the changes made and describe how they were tested.
- Include any limitations or known issues in the description.
- Ensure all new features are compliant with the Cashu specification (NUTs) provided above.
