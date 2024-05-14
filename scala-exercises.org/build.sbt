import Dependencies._

ThisBuild / scalaVersion := "2.13.12"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.s0x6fup"
ThisBuild / organizationName := "s0x6fup"

lazy val root = (project in file("."))
  .settings(
    name := "scala-exercises.org",
    libraryDependencies += munit % Test
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
