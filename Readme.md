# SBT Gherkin Converter

SBT auto plugin to use [Gherkin Converter](https://github.com/randomcoder/gherkin-converter) to generate html from
`.feature` files.

### How to use

`sbt-gherkin-converter` is an auto plugin. This means you need SBT 0.13.5 or higher. If you are using an earlier 
0.13.x build, you should be able to upgrade to 0.13.5 without any issues.

Add the plugin to your build with the following in `project/plugins.sbt`:

```
addSbtPlugin("uk.co.randomcoding" % "sbt-gherkin-converter" % "0.0.2")
```

Then in your build sbt, set the location of the parent directory of your feature files _**relative to the project's root**_:

```
featuresDir in (Compile, writeFeatures) := "src/main/resources/feature"
```

That's it! You can now generate html versions of your `.feature` files with the `writeFeatures` task:

```
> writeFeatures
Writing features from src/main/resources/feature to ~/code/test-project/target/featurehtml
Written html for feature files to ~/code/test-project/target/featurehtml

```
