# SBT Gherkin Converter

SBT auto plugin to use [Gherkin Converter](https://github.com/randomcoder/gherkin-converter) to generate html from
`.feature` files.

### How to use

`sbt-gherkin-converter` is an auto plugin. This means you need SBT 0.13.5 or higher. If you are using an earlier 
0.13.x build, you should be able to upgrade to 0.13.5 without any issues.

Add the plugin to your build with the following in `project/plugins.sbt`:

```
addSbtPlugin("uk.co.randomcoding" % "sbt-gherkin-converter" % "0.2.1")
```

Then in your build sbt, set the location of the parent directory of your feature files _**relative to the project's root**_
and the title you want for the main index page

```
featuresDir in (Compile, writeFeatures) := "src/main/resources/feature"

featuresTitle in (Compile, writeFeatures) := "Your Title"
```

If your feature files live in the `test` scope then use

```
featuresDir in (Test, writeFeatures) := "src/main/resources/feature"

featuresTitle in (Test, writeFeatures) := "Your Title"
```

That's it! You can now generate html versions of your `.feature` files with the `writeFeatures` task:

### Multi Project Builds
If you have a build with multiple projects then you will typically want to disable the task for the `root` project

```
lazy val root = project.in(file(".")).
  settings(...).
  settings(
    writeFeatures := {}
  )
```

The same is true for other projects that you don't want to execute the task in, e.g. because they don't contain any
features.

```
> writeFeatures
Writing features from src/main/resources/feature to ~/code/test-project/target/featurehtml
Written html for feature files to ~/code/test-project/target/featurehtml
```

### Licence

This plugin is licenced under the [AGPL v3](https://www.gnu.org/licenses/agpl-3.0.en.html)

### Contributions

Contributions are welcome. Please create a fork and submit pull requests.
