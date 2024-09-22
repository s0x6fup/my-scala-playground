package com.stupidbird

import scala.io.StdIn
import akka.actor.typed.ActorSystem
import scala.concurrent.ExecutionContext
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Route.seal
import scala.util.{Failure, Success}
import com.stupidbird.routers._
import com.stupidbird.utils.SessionService._
import com.stupidbird.utils.DatabaseInitializer
import scalikejdbc._
import java.util.UUID.randomUUID

object StupidbirdService extends App {
  private val host = "127.0.0.1"
  private val port = 9001
  private val databaseUrl = "jdbc:mysql://127.0.0.1:3306/stupidbird"
  private val databaseUser = "stupidbird_user"
  private val databasePassword = "myverysecretpassword123!"

  Class.forName("com.mysql.cj.jdbc.Driver")
  ConnectionPool.singleton(databaseUrl, databaseUser, databasePassword)

  implicit val dbSession: DBSession = AutoSession
  implicit val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "http-server-system")
  implicit val executionContext: ExecutionContext = system.executionContext

  DatabaseInitializer.init()

  private val allRouters = getUserSession { implicit callScope =>
    HealthRouter() ~
    AuthenticationRouter() ~
    PostRouter() ~
    CommentRouter()
  }

  private val bindingFuture = Http().newServerAt(host, port).bind(allRouters)
  println(s"[+] listening on http://$host:$port/ press RETURN to terminate app")
  StdIn.readLine()
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
