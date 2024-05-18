package com.stupidbird

import akka.actor.typed.ActorSystem
import scala.concurrent.ExecutionContext
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Route.seal
import scala.util.{Success, Failure}

object StupidbirdService extends App {
  val host = "127.0.0.1"
  val port = 9001

  implicit val system: ActorSystem[Any] =
    ActorSystem(Behaviors.empty, "http-server-system")
  implicit val executionContext: ExecutionContext = system.executionContext

  val router: Route = concat(
    pathPrefix("_api" / "health")(HealthRouter())
  )

  val httpBindingFuture = Http().newServerAt(host, port).bind(router)
}
