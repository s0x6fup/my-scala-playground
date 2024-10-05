package com.stupidbird.authorization

import scala.io.StdIn
import akka.actor.typed.ActorSystem
import scala.concurrent.ExecutionContext
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import com.stupidbird.utils.SessionClient.extractUserSession
import com.stupidbird.authorization.routers.AuthorizationRouter
import com.stupidbird.utils.UserSession

object AuthorizationServer extends App {
  private val host = "127.0.0.1"
  private val port = 9002

  implicit val system: ActorSystem[Any] =
    ActorSystem(Behaviors.empty, "AuthorizationServer")
  implicit val executionContext: ExecutionContext = system.executionContext

  private val allRouters = extractUserSession { implicit callScope: UserSession =>
    AuthorizationRouter()
  }

  private val bindingFuture = Http().newServerAt(host, port).bind(allRouters)
  println(s"[+] listening on http://$host:$port/ press RETURN to terminate app")
  StdIn.readLine()
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
