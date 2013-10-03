seq(cucumberSettings : _*)

cucumberMaxMemory := "1024M"

cucumberStepsBasePackage := "es.weso.reconciliator"

// Maven Central Specifics

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/weso/wiFetcher</url>
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/weso/CountryReconciliator</url>
    <connection>scm:git:git@github.com:weso/CountryReconciliator.git</connection>
  </scm>
  <developers>
    <developer>
      <name>Ignacio Fuertes Bernardo</name>
      <organization>WESO</organization>
      <url>https://es.linkedin.com/pub/ignacio-fuertes-bernardo/46/146/2a8/</url>
      <organizationUrl>http://www.weso.es</organizationUrl>
      <roles>
        <role>architect</role>
        <role>developer</role>
        <role>tester</role>
      </roles>
     </developer>
    <developer>
      <name>César Luis Alvargonzález</name>
      <organization>WESO</organization>
      <url>http://www.cesarla.com</url>
      <organizationUrl>http://www.weso.es</organizationUrl>
      <roles>
        <role>developer</role>
        <role>tester</role>
      </roles>
     </developer>
  </developers>)