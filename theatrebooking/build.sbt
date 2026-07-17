ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.5.2"

lazy val root = (project in file("."))
  .settings(
    name := "theatrebooking",
    libraryDependencies ++= Seq(
      "com.github.nscala-time" %% "nscala-time" % "2.34.0",
      //"io.jvm.uuid" %% "scala-uuid" % "0.3.1",
      "com.lihaoyi" %% "pprint" % "0.9.0"
    )
  )

