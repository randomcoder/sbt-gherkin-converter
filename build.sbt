name := "sbt-gherkin-converter"

version := "0.2.4"

scalaVersion := "2.10.6"

organization := "uk.co.randomcoding"

sbtPlugin := true

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies += "uk.co.randomcoding" %% "gherkin-converter" % "0.6.3"

licenses += ("AGPLv3", url("https://www.gnu.org/licenses/agpl-3.0.en.html"))

publishMavenStyle := false

bintrayRepository := "sbt-plugins"

bintrayOrganization in bintray := None

bintrayVcsUrl := Some("git@github.com:randomcoder/sbt-gherkin-converter.git")
