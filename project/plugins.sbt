logLevel := Level.Warn

addSbtPlugin("com.typesafe.sbteclipse" %% "sbteclipse-plugin" % "2.3.0")

resolvers += "Templemore Repository" at "http://templemore.co.uk/repo/"

addSbtPlugin("templemore" %% "sbt-cucumber-plugin" % "0.8.0")
