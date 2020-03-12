name := "VendingMachine"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.3"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.6.3" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.6.3" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0-M4" % Test
