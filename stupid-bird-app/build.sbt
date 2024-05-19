import Dependencies._

ThisBuild / scalaVersion := "2.12.19"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.stupidbird"
ThisBuild / organizationName := "stupidbird"

lazy val akkaHttpVersion = "10.2.8"
lazy val akkaVersion = "2.6.9"
lazy val circeVersion = "0.14.1"
lazy val specs2Version = "4.3.6"

lazy val root = (project in file("."))
  .settings(
    name := "stupid-bird-app",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.scalikejdbc" %% "scalikejdbc" % "2.5.2",
      "mysql" % "mysql-connector-java" % "8.0.33",
      "ch.qos.logback" % "logback-classic" % "1.5.6",
      "org.specs2" %% "specs2-core" % specs2Version % Test
    )
  )
