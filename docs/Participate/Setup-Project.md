---
icon: material/flag
---
The BetonQuest Organisation recommends [IntelliJ](https://www.jetbrains.com/idea/)
(Community Edition) as the IDE (Integrated Development Environment).
The advantage of using IntelliJ is that this guide contains some steps and the project contains some files
that help to fulfill our requirements regarding code and documentation style.
You can still use your preferred IDE, but then you need to check on your own that your changes fulfill our requirements.

##Installing IntelliJ 
First download [IntelliJ](https://www.jetbrains.com/idea/download) and install it.

After you installed IntelliJ, we recommend installing the plugin
[Save Actions](https://plugins.jetbrains.com/plugin/7642-save-actions).
The plugin automatically formats code, organizes imports, adds final modifiers, and fulfils some other requirements we have.
You don't need to configure that plugin, the project contains the configuration file.

## Check out the repository
You need a Git installation to be able to check out code from GitHub.
You can follow this [guide](https://docs.github.com/en/get-started/quickstart/set-up-git)
if you don't know how to install Git.  

Then you should [fork](https://docs.github.com/en/get-started/quickstart/fork-a-repo)
the BetonQuest repository to your own account on GitHub.

After you have set up the IDE,
[clone](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository-from-github/cloning-a-repository)
the BetonQuest repository from your account. You can also directly
[clone the repository in IntelliJ](https://blog.jetbrains.com/idea/2020/10/clone-a-project-from-github/).

??? "In case videos and images are missing after cloning"
    We use [Git LFS](https://git-lfs.github.com/) to store big files like media files, so you need to install that too.
    Once you have executed the file that you downloaded from the Git LFS website, just run `git lfs install`.
    Then use `git lfs pull` to actually download the files.

### Adding remote repository
In IntelliJ click on `Git` in the left upper corner and then `Manage Remotes...`.
In the new window you already see a remote called `origin`. This remote is your fork of BetonQuest.
Now add a new repository with the name `upstream` and the url `https://github.com/BetonQuest/BetonQuest.git`.

## IntelliJ settings
Formatting for .md (Markdown) files can break some features of
[Material for MkDocs](https://squidfunk.github.io/mkdocs-material), so we disable it for these files.
Go to `File/Settings/Editor/Code Style` then go to the `Formatter` tab and add `*.md` to the `Do not format:` field.

In `File/Settings/Editor/Code Style/Java` navigate to the `Imports` tab.
You will now configure when to use star imports, in general we don't want them at all, but there are some exceptions.
Set `Class count to use import with '*':` and `Names count to use static import with '*':` to `9999999`.
And under `Packages to Use Import with '*'` configure the following:

|           Static           |             Package              |         With Subpackages          |
|:--------------------------:|:--------------------------------:|:---------------------------------:|
| :material-checkbox-marked: |   org.mockito.ArgumentMatchers   | :material-checkbox-blank-outline: |
| :material-checkbox-marked: | org.junit.jupiter.api.Assertions | :material-checkbox-blank-outline: |
| :material-checkbox-marked: |       org.mockito.Mockito        | :material-checkbox-blank-outline: |

Now we enable some automatic checks, when you commit things, that ensures everything is fine.
In the `Commit` tab click on the three settings dots, near the `Amend` checkbox and then check the following entries under `Before Commit`:

- Reformat Code
- Rearrange Code
- Optimize Imports
- Analyze Code
- Check TODO (Show All)

## Building the Plugin jar
You can build the plugin with Maven. Sometimes, IntelliJ auto-detects that BetonQuest is a Maven project. You can see
a "Maven" tab on the right side of the editor if that's the case. Otherwise, do this:
First, open the "Project" tab on the left site. Then right-click the `pom.xml` file in the projects root folder. 
Select "Add as Maven Project". 

To build the BetonQuest jar, you simply need to run `mvn verify`.
You can do this from the command line or use IntelliJ's `Maven` tab (double-click on `BetonQuest/Lifecycle/verify`).
You can then find a `BetonQuest.jar` in the newly created folder `/target/artifacts`.

### Build speed up
As BetonQuest has a lot of dependencies, the build can take a long lime, especially for the first build.
You can speed this up with the following configuration, that downloads all dependencies from our own Repository Manager
instead of searching through all repositories that are defined in the project.

If you do not already have the file, create a new file in this location: `<HOME DIRECTORY>\.m2\settings.xml`.
The home directory on Windows is `C:\Users\<YOUR USER NAME>`.
Then adopt or copy the following into the file:

````XML linenums="1"
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <mirrors>
    <mirror>
      <id>BetonQuest-Mirror</id>
      <url>https://betonquest.org/nexus/repository/default/</url>
      <mirrorOf>betonquest-papermc-repo,betonquest-enginehub-repo,betonquest-heroes-repo,betonquest-lumine-repo,betonquest-citizensnpcs-repo,betonquest-codemc-repo,betonquest-placeholderapi-repo,betonquest-dmulloy2-repo,betonquest-lichtspiele-repo,betonquest-elmakers-repo,betonquest-jitpack-repo,betonquest-sonatype-releases-repo,betonquest-sonatype-snapshots-repo,betonquest-minecraft-repo</mirrorOf>
    </mirror>
  </mirrors>

</settings>
````

### Build on Start
The first build of a day can take a while, because every version gets re-checked once every day.
This is the reason, why an automatic build on startup reduces the time of following builds. It is really worth it to set it up.
In IntelliJ navigate to `File/Settings/Tools/Startup Tasks` click on the `Add` button and click `Add New Config`.
Now select `Maven`, set a `Name` like `BetonQuest Resolve Dependencies` and write `dependency:resolve`
into the field `Command line`. Then confirm with `Ok` twice.
Now after starting IntelliJ the `BetonQuest Resolve Dependencies` task should run automatically.



##Building the Documentation
Make sure [Python3](https://www.python.org/downloads/) is installed on your local system
and added to the PATH environment variable. The Python installer allows you to do so with a checkbox called something like
"Add Python to environment variables".
You also need to install [GTK](https://www.gtk.org/), the easiest way is to use this 
[GTK installer](https://github.com/tschoonj/GTK-for-Windows-Runtime-Environment-Installer/) if you are on Windows. 

Install all other dependencies by entering `pip install -r config/docs-requirements.txt` in the terminal on the project's root directory.

??? "In case you are a [Material for MkDocs](https://squidfunk.github.io/mkdocs-material) insider (paid premium version)"  
    Set your license key by executing `setx MKDOCS_MATERIAL_INSIDERS LICENSE_KEY_HERE /M` (Windows) in the terminal.
    Now you need to restart IntelliJ for the changes to take effect. 
    Then run `pip install -r config/docs-requirements-insiders.txt` instead of `docs-requirements.txt`.

### See your changes live
Run this command in IntelliJ's integrated terminal (at the bottom) to create a docs preview in your browser:

```BASH
mkdocs serve
```

Then visit [127.0.0.1:8000](http://127.0.0.1:8000) to make sure that everything works.

---
## Where to Continue?
You should now continue with [Create a new Branch](Process/Create-a-new-Branch.md),
before you start changing [Code](Process/Code/Workflow.md) or [Docs](Process/Docs/Workflow.md).
