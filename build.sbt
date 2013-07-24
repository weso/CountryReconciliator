name := "countryreconciliator"

scalaVersion := "2.10.1"

version := "0.0.1-SNAPSHOT"

seq(cucumberSettings : _*)

cucumberMaxMemory := "1024M"

cucumberStepsBasePackage := "es.weso.reconciliator"
