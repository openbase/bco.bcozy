# BCozy Project - A locationbased smart home user interface

## Development Rules
The development rules must be fulfilled to allow project build with maven.
If violations are noticed, the build will be aborted!

### Checkstyle:
A checkstyle configuration is provided in the codecheck folder

### Static Code Analysis:
To avoid common programming flaws a pmd configuration is also provided in the codecheck folder.

## IDE Usage

### IntelliJ Idea
We provide IDE settings for IntelliJ Idea in the ide folder.
They can be imported via File > Import Settings.
We recommend installing the CheckStyle-IDEA and the PMD plugin.
Maven build options can be shown if View > Tool Windows > Maven Projects is activated.
PMD Test can be run if you activate View > Tool Windows > PMD.
Make sure the right ruleset is applied (File > Project Settings > Other Settings > PMD).
Checkstyle can be scanned manually if you activate View > Tool Windows > CheckStyle.
Make sure the right Rules are applied (BCozy) (File > Project Settings > Other Settings > Checkstyle).

### Other IDEs
Support for eclipse might be provided in the future.

## Build systems

### Maven
The general project setup was done with maven.
Using the Maven exec plugin one can execute the project directly within IntelliJ Idea.
For that create a Run/Debug Configuration from type Maven. Choose exec:exec for the
command line entry (and add "Before launch:" Maven goal compiler:compile).

### Gradle
In the future a gradle configuration will provided to allow generation of android apks.
