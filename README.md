# BCozy Project - A location based smart home user interface

## Development Rules
The development rules must be fulfilled to allow project build with maven.
If violations are noticed, the build will be aborted!

### Checkstyle:
A checkstyle configuration is provided in the codecheck folder

### Static Code Analysis:
To avoid common programming flaws a PMD configuration is also provided in the codecheck folder.

## IDE Usage

### IntelliJ Idea
We provide IDE settings for IntelliJ Idea in the `ide` folder.
They can be imported via File > Import Settings.

We recommend installing the CheckStyle-IDEA and the PMD plugin.

Maven build options can be shown if View > Tool Windows > Maven Projects is activated.

PMD Test can be run if you activate View > Tool Windows > PMD.
Make sure the right rule set is applied (File > Project Settings > Other Settings > PMD).

Checkstyle can be scanned manually if you activate View > Tool Windows > CheckStyle.
Make sure the right rules are applied (BCozy) (File > Project Settings > Other Settings > Checkstyle).
When importing the Checkstyle rules be aware to insert the correct directory (the codecheck folder) for the `$config_loc` variable in the corresponding IntelliJ prompt.

### Other IDEs
Support for eclipse might be provided in the future.

## Build systems

### Maven
The general project setup was done with maven.

Using the Maven exec plugin one can execute and debug the project directly within IntelliJ Idea.
Therefor create a Run/Debug Configuration from type Maven. Set the command line entry as follows:

```
exec:java -Dexec.mainClass=org.dc.bco.bcozy.BCozy
```

Additionally add the following "Run Maven" goal in the "Before launch:"-section:

```
compiler:compile
```

### Gradle
In the future a gradle configuration will be provided to allow generation of android apks.
