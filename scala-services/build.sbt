name := "stupid bird"
ThisBuild / scalaVersion := "2.12.19"
ThisBuild / version := "0.1.0"
ThisBuild / organization := "com.stupidbird"

/*
 * PROJECTS
 *
 * to execute run `sbt "project authorization" run`
 */
lazy val common = (project in file("common") /* the location in the repo */)
  .settings(
    name := "common",
    libraryDependencies ++= commonDependencies
  )

lazy val authorization = (project in file("authorization"))
  .settings(
    name := "authorization",
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(
    common // the authorization projects needs the common project to compile as well
  )

lazy val playground = (project in file("playground"))
  .settings(
    name := "playground",
    libraryDependencies ++= Seq(
      dependencies.akkaStream,
      dependencies.akkaHttp,
      dependencies.akkaHttpSprayJson,
      dependencies.akkaActorTyped
    )
  )

/*
 * DEPENDENCIES
 *
 * This way it is easier to import different dependencies in different projects in a modular manner and keeping
 * version handling simple.
 */
lazy val dependencies =
  new {
    val akkaHttpVersion = "10.5.3"
    val akkaVersion = "2.8.5"
    val circeVersion = "0.14.7"
    val scalikejdbcVersion = "4.2.1"

    val akkaActorTyped = "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion
    val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
    val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
    val akkaStreamTestkit = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
    val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion
    val akkaHttpSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
    val circeCore = "io.circe" %% "circe-core" % circeVersion
    val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
    val circeParser = "io.circe" %% "circe-parser" % circeVersion
    val scalikejdbc = "org.scalikejdbc" %% "scalikejdbc" % scalikejdbcVersion
    val scalikejdbcInterpolation = "org.scalikejdbc" %% "scalikejdbc-interpolation" % scalikejdbcVersion
    val scalikejdbcSyntaxSupportMacro = "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % scalikejdbcVersion
    val scalaBcrypt = "com.github.t3hnar" %% "scala-bcrypt" % "4.3.0"
    val jwtCore = "com.github.jwt-scala" %% "jwt-core" % "10.0.1"
    val mysqlConnectorJava = "mysql" % "mysql-connector-java" % "8.0.33"
    val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.5.6"
    val specs2Core = "org.specs2" %% "specs2-core" % "4.3.6"
  }

lazy val commonDependencies = Seq(
  dependencies.akkaActorTyped,
  dependencies.akkaStream,
  dependencies.akkaHttp,
  dependencies.akkaStreamTestkit,
  dependencies.akkaHttpTestkit,
  dependencies.akkaHttpSprayJson,
  dependencies.circeCore,
  dependencies.circeGeneric,
  dependencies.circeParser,
  dependencies.scalikejdbc,
  dependencies.scalikejdbcInterpolation,
  dependencies.scalikejdbcSyntaxSupportMacro,
  dependencies.scalaBcrypt,
  dependencies.jwtCore,
  dependencies.mysqlConnectorJava,
  dependencies.logbackClassic,
  dependencies.specs2Core % "test"
)
