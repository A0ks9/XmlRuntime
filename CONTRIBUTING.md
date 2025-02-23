# Contributing to DynamicInflate

We welcome contributions to DynamicInflate!  Whether you're reporting bugs, suggesting new features,
improving documentation, or submitting code changes, your help is highly appreciated.

Please take a moment to review this guide before contributing to ensure a smooth and effective
contribution process.

## Getting Started

1. **Fork the Repository:** Start by forking the DynamicInflate repository to your own GitHub
   account. This creates a personal copy of the project where you can make changes.

2. **Clone Your Fork:** Clone your forked repository to your local development machine:

   ```bash
   git clone https://github.com/A0ks9/Voyager.git
   cd DynamicInflate
   ```

3. **Set Up Your Development Environment:**
    * **Android Studio:** Ensure you have the latest stable version of Android Studio installed.
    * **Android SDK:**  DynamicInflate targets [Specify Minimum SDK Version, e.g., API 21]. Make
      sure you have the necessary Android SDK components installed via the SDK Manager in Android
      Studio.
    * **Kotlin:** DynamicInflate is written in Kotlin. Familiarity with Kotlin is essential.
    * **Gradle:** DynamicInflate uses Gradle as its build system. Android Studio will handle Gradle
      setup automatically.

4. **Build the Project:** Open the project in Android Studio. Gradle will automatically sync and
   download dependencies. You can build the project from Android Studio (Build -> Make Project) or
   using the Gradle command line:

   ```bash
   ./gradlew build
   ```

5. **Run the Example App (Optional but Recommended):**  Run the example application module (`app`)
   to get a feel for how DynamicInflate works and to test your changes.

## Contribution Guidelines

We welcome various types of contributions:

* **Bug Reports:** If you encounter a bug, please submit a detailed bug report. Include:
    * A clear and descriptive title.
    * Steps to reproduce the bug.
    * Expected behavior vs. actual behavior.
    * Device information (Android version, device model).
    * Library version (if applicable).
    * Relevant code snippets, logcat output, or screenshots if helpful.

* **Feature Requests:**  Have a great idea for a new feature or enhancement? Submit a feature
  request!  Describe:
    * The feature you are proposing.
    * Why you think it's valuable for DynamicInflate users.
    * Potential use cases or benefits.

* **Code Contributions (Bug Fixes, New Features, Improvements):**  We gladly accept code
  contributions. Please follow these guidelines:
    * **Coding Style:**  Follow the existing Kotlin coding style used in the project. Generally, we
      adhere to the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
      and Android Kotlin Style Guide. Android Studio's code formatting tools can help you with
      this (Code -> Reformat Code).
    * **Keep Changes Focused:**  Each pull request should ideally address a single bug fix or
      feature. Smaller, focused PRs are easier to review and merge.
    * **Write Tests:**  If you are adding new features or fixing bugs, please include unit tests or
      integration tests to verify your changes. Tests help ensure code quality and prevent
      regressions.
    * **Document Your Code:**  Document any new classes, functions, or significant code changes
      using KDoc-style comments.
    * **Commit Messages:**  Write clear and concise commit messages. Follow
      the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification for
      commit message formatting. This helps with changelog generation and release management.
      Example:

      ```
      feat(core): Add support for layout includes

      Implement layout includes/templates in JSON and XML layouts.
      This allows users to define reusable layout snippets, improving
      layout modularity and reducing duplication.

      BREAKING CHANGE: Layout format for include declarations has been updated.
      ```

    * **Pull Request Description:**  When submitting a pull request, provide a clear and detailed
      description of your changes. Explain the problem you are solving, the feature you are adding,
      or the improvement you are making. Reference any related issues.

* **Documentation Improvements:**  Help us improve the documentation!  This could include:
    * Fixing typos or grammatical errors.
    * Adding more detailed explanations or examples in the `README.md`.
    * Creating tutorials or guides on specific features.
    * Improving KDoc documentation within the code.

## Workflow for Code Contributions

1. **Create a Branch:** Before starting to work on a code change, create a new branch from the
   `main` branch in your forked repository. Use a descriptive branch name, for example:
   `fix/bug-in-attribute-parsing` or `feat/data-binding-support`.

   ```bash
   git checkout -b fix/bug-in-attribute-parsing main
   ```

2. **Make Your Changes:** Implement your bug fix, feature, or improvement in your branch. Commit
   your changes frequently and with clear commit messages.

3. **Test Your Changes:** Build the project and run the example app to test your changes thoroughly.
   Make sure your changes don't introduce new issues and that existing functionality is not broken.
   Run unit tests if you've added or modified code that has tests.

4. **Rebase (Optional but Recommended):** Before submitting a pull request, it's often helpful to
   rebase your branch on top of the latest `main` branch. This helps keep the commit history clean
   and avoids merge conflicts.

   ```bash
   git fetch origin
   git rebase origin/main
   ```
   *(If you encounter conflicts during rebasing, resolve them.)*

5. **Submit a Pull Request:** Once you are satisfied with your changes, push your branch to your
   forked repository:

   ```bash
   git push origin fix/bug-in-attribute-parsing
   ```

   Then, go to the original DynamicInflate repository on GitHub and click the "Compare & pull
   request" button to create a new pull request from your branch to the `main` branch.

6. **Pull Request Review:** Your pull request will be reviewed by maintainers. Be prepared to
   address feedback and make revisions if necessary. We aim to review PRs in a timely manner.

7. **Merge:** Once your pull request is approved and passes all checks, it will be merged into the
   `main` branch. Congratulations, you've contributed to DynamicInflate!

## Code of Conduct

We are committed to fostering a welcoming and inclusive community. Please be respectful and
considerate of others in your interactions within this project. We expect all contributors to adhere
to a basic code of conduct that promotes a positive and collaborative environment.  (*(You can
optionally link to a more detailed Code of Conduct document if you have one)*).

## License

By contributing to DynamicInflate, you agree that your contributions will be licensed under
the [Apache 2.0 License](LICENSE).

Thank you for your contributions! We appreciate your help in making DynamicInflate a better library
for the Android community.

---