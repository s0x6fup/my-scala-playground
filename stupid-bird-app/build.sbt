import Dependencies._

ThisBuild / scalaVersion := "2.12.19"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.stupidbird"
ThisBuild / organizationName := "stupidbird"

lazy val akkaHttpVersion = "10.5.3"
lazy val akkaVersion = "2.8.5"
lazy val circeVersion = "0.14.7"
lazy val scalikejdbcVersion = "4.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "stupid-bird-app",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.scalikejdbc" %% "scalikejdbc" % scalikejdbcVersion,
      "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % scalikejdbcVersion,
      "mysql" % "mysql-connector-java" % "8.0.33",
      "ch.qos.logback" % "logback-classic" % "1.5.6",
      "org.specs2" %% "specs2-core" % "4.3.6" % Test
    )
  )
