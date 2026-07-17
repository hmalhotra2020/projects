ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.2"

lazy val root = (project in file("."))
  .settings(
    name := "RockingWallet",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "scalasql" % "0.3.0",
      "com.lihaoyi" %% "scalasql-namedtuples" % "0.2.3",
      "com.lihaoyi" %% "os-lib" % "0.11.8",
      "org.postgresql" % "postgresql" % "42.7.10",
      "com.lihaoyi" %% "pprint" % "0.9.6"
    )
  )
