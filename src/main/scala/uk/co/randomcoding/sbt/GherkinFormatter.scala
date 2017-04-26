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

import sbt.Keys.{target, streams}
import sbt.{AutoPlugin, settingKey, taskKey, _}
import uk.co.randomcoding.cucumber.generator.html.HtmlFeatureGenerator

object GherkinFormatter extends AutoPlugin {
  override def trigger = allRequirements

  object autoImport {

    val writeFeatures = taskKey[Unit]("Test Task")
    val featuresOutput = settingKey[File]("Location of html output from feature files")
    val featuresDir = settingKey[String]("Directory containing the feature files")
    val featuresTitle = settingKey[String]("The title for the main page")

    lazy val gherkinFormatterSettings: Seq[Def.Setting[_]] = Seq(
      featuresOutput in writeFeatures := (target.value / "featurehtml"),
      featuresDir in writeFeatures := "src/main/resources/feature",
      featuresTitle in writeFeatures := "Features",
      writeFeatures := {
        val featureDir = new File((featuresDir in writeFeatures).value)
        val targetDirectory = (featuresOutput in writeFeatures).value
        val mainTitle = (featuresTitle in writeFeatures).value
        streams.value.log.info(s"Generating html for features from $featureDir into $targetDirectory")
        WriteFeatureHtml(featureDir, targetDirectory, mainTitle)
        streams.value.log.info(s"Successfully generated html for features from $featureDir into $targetDirectory")
      }
    )
  }

  import autoImport._
  override val projectSettings = inConfig(Compile)(gherkinFormatterSettings)
}

object WriteFeatureHtml {

  def apply(featureDir: File, featuresOutput: File, mainTitle: String) = {
    val generator = new HtmlFeatureGenerator()
    generator.generateFeatures(featureDir, featuresOutput)
    generator.generateIndexes(featuresOutput, mainTitle)
  }
}
