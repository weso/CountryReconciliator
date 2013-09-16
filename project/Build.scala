import sbt._
import sbt.Keys._

object ApplicationBuild extends Build {
  
  val junitV = "4.11"
  val cucumberV = "1.1.4"
  val scalatestV = "2.0.M7"
  val scalaV = "2.10.2"

  lazy val countryReconciliator = Project(
    id = "countryreconciliator",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "CountryReconciliator",
      organization := "es.weso",
      version := "0.2.0-SNAPSHOT",
      scalaVersion := scalaV,

      /*Test Dependancies*/
      libraryDependencies += "junit" % "junit" % junitV,
  
      libraryDependencies += "org.scalatest" %% "scalatest" % scalatestV,
      libraryDependencies += "info.cukes" %% "cucumber-scala" % cucumberV,
      
      
      libraryDependencies += "info.cukes" % "cucumber-jvm" % cucumberV,
      libraryDependencies += "info.cukes" % "cucumber-core" % cucumberV,
      libraryDependencies += "info.cukes" % "cucumber-junit" % cucumberV,
      
      /*Java Dependancies*/
      libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.32.0",
      libraryDependencies += "commons-configuration" % "commons-configuration" % "1.9",
      libraryDependencies += "org.apache.lucene" % "lucene-core" % "4.0.0",
      libraryDependencies += "org.apache.solr" % "solr-core" % "4.0.0",  
      libraryDependencies += "log4j" % "log4j" % "1.2.17",
            
        
      resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
      resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      resolvers += "Templemore Repository" at "http://templemore.co.uk/repo/")
    )
}