/*
 * Copyright (C) 2017. RandomCoder <randomcoder@randomcoding.co.uk>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.randomcoding.sbt

import java.io.FileWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files

import sbt.Keys.target
import sbt.{AutoPlugin, settingKey, taskKey, _}
import uk.co.randomcoding.cucumber.generator.reader.FeatureReader
import uk.co.randomcoding.cucumber.generator.writer.FeatureHtml

import scala.collection.JavaConverters._
import scala.util.Try
import scala.xml.{NodeSeq, XML}

object GherkinFormatter extends AutoPlugin {
  override def trigger = allRequirements

  object autoImport {

    val writeFeatures = taskKey[Unit]("Test Task")
    val featuresOutput = settingKey[File]("Location of html output from feature files")
    val featuresDir = settingKey[String]("Directory containing the feature files")

    lazy val gherkinFormatterSettings: Seq[Def.Setting[_]] = Seq(
      writeFeatures := WriteFeatureHtml(new File((featuresDir in writeFeatures).value), (featuresOutput in writeFeatures).value),
      featuresOutput in writeFeatures := (target.value / "featurehtml"),
      featuresDir in writeFeatures := "src/main/resources/feature"
    )
  }

  import autoImport._
  override val projectSettings = inConfig(Compile)(gherkinFormatterSettings)

}

object WriteFeatureHtml {

  def apply(featureDir: File, featuresOutput: File) = {
    if (featuresOutput.exists) IO.delete(featuresOutput)

    println("Writing features from " + featureDir + " to " + featuresOutput)

    val relativeBase = featureDir
    writeFeaturesIn(featureDir, featuresOutput, relativeBase)
    writeIndexFiles(featuresOutput, false)

    println("Written html for feature files to " + featuresOutput)
  }

  private[this] def writeFeaturesIn(dir: File, baseOutputDir: File, relativeTo: File): Unit = {
    println("Writing features from " + dir + " to " + baseOutputDir)
    val relativePath = dir.relativeTo(relativeTo).map(_.getPath).getOrElse("")
    val targetDir = new File(baseOutputDir, relativePath)

    val dirContents = Try(dir.listFiles.toList).getOrElse(Nil)

    dirContents.partition(_.isDirectory) match {
      case (dirs, files) => {
        writeFeatures(files.filter(_.getName.endsWith(".feature")), targetDir)
        dirs.foreach(writeFeaturesIn(_, baseOutputDir, relativeTo))
      }
    }
  }

  private[this] def writeFeatures(features: Seq[File], outputDir: File) = {
    outputDir.mkdirs()
    features.foreach { featureFile =>
      val html = FeatureHtml(FeatureReader.read(Files.readAllLines(featureFile.toPath, StandardCharsets.UTF_8).asScala.toList))
      val targetFile = outputDir / (featureFile.getName + ".html")

      writeFile(html, targetFile)
    }
  }

  private[this] def writeIndexFiles(htmlDir: File, linkToParent: Boolean): Unit = {
    val htmlFeatureFiles = htmlDir.list().filter(_.endsWith(".feature.html"))
    val filesLinks = htmlFeatureFiles.map(file => <li><a href={file}>{file.takeWhile(_ != '.').replaceAll("""([A-Z0-9])""", """ $1""").trim}</a></li>)
    val dirLinks = htmlDir.listFiles().filter(_.isDirectory).map{ dir => <li><a href={dir.name + "/index.html"}>{dir.name.capitalize}</a></li> }
    val parentLink = if (linkToParent) <li><a href="../index.html">Up</a></li> else NodeSeq.Empty
    val linksList = <ul>{parentLink ++ filesLinks ++ dirLinks}</ul>

    writeFile(linksList, htmlDir / "index.html")

    htmlDir.listFiles().filter(_.isDirectory).foreach(writeIndexFiles(_, true))
  }

  private[this] def writeFile(html: NodeSeq, targetFile: File) = {
    val writer = new FileWriter(targetFile)
    writer.write("<!DOCTYPE html>\n")
    XML.write(writer, html.head, "UTF-8", false, null)
    writer.close()
  }
}
