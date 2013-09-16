import sbt._
import sbt.Keys._

object ApplicationBuild extends Build {

  val AppName = "CountryReconciliator"
  val AppOrg = "es.weso"
  val AppVersion = "0.2.0-SNAPSHOT"
    
  val ScalaV = "2.10.2"

  /**
   * Dependancies Versions
   */
  val ConfigV = "1.9"
  val CucumberV = "1.1.4"
  val JunitV = "4.11"
  val Log4jV = "1.2.17"
  val SeleniumV = "2.35.0"
  val ScalatestV = "2.0.M7"
  val LuceneV = "4.0.0"
  val TypeConfigV = "1.0.1"

  lazy val countryReconciliator = Project(
    id = AppName.toLowerCase,
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
        
      name := AppName,
      organization := AppOrg,
      version := AppVersion,
      
      scalaVersion := ScalaV,

      /*Test Dependencies*/
      libraryDependencies += "junit" % "junit" % JunitV,
      libraryDependencies += "info.cukes" % "cucumber-jvm" % CucumberV,
      libraryDependencies += "info.cukes" % "cucumber-core" % CucumberV,
      libraryDependencies += "info.cukes" % "cucumber-junit" % CucumberV,

      libraryDependencies += "org.scalatest" %% "scalatest" % ScalatestV,
      libraryDependencies += "info.cukes" %% "cucumber-scala" % CucumberV,

      /*Java Dependencies*/
      libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % SeleniumV,
      libraryDependencies += "commons-configuration" % "commons-configuration" % ConfigV,
      libraryDependencies += "com.typesafe" % "config" % TypeConfigV,
      libraryDependencies += "org.apache.lucene" % "lucene-core" % LuceneV,
      libraryDependencies += "org.apache.solr" % "solr-core" % LuceneV,
      libraryDependencies += "log4j" % "log4j" % Log4jV,

      /*Exterrn Repositories*/
      resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
      resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      resolvers += "Templemore Repository" at "http://templemore.co.uk/repo/",

      /*Local Repositories*/
      resolvers += Resolver.url("Local Ivy Repository", url("file://" + Path.userHome.absolutePath + "/.ivy2/local/"))(Resolver.ivyStylePatterns),
      resolvers += "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"))
}
