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
import com.stupidbird.routers._
import scalikejdbc._

object StupidbirdService extends App {
  val host = "127.0.0.1"
  val port = 9001
  val databaseUrl = "jdbc:mysql://localhost:3306/stupidbird"
  val databaseUser = "stupidbird_user"
  val databasePassword = "myverysecretpassword123!"

  Class.forName("com.mysql.jdbc.Driver")
  ConnectionPool.singleton(
    databaseUrl,
    databaseUser,
    databasePassword
  )

  implicit val dBsession: DBSession = AutoSession

  val resultTest: Option[Int] = DB.readOnly { implicit dBsession =>
    sql"select 1".map(rs => rs.int(1)).single.apply()
  }
  println(resultTest)

  implicit val system: ActorSystem[Any] =
    ActorSystem(Behaviors.empty, "http-server-system")
  implicit val executionContext: ExecutionContext = system.executionContext

  val router: Route = concat(
    pathPrefix("_api" / "health")(HealthRouter())
  )

  val httpBindingFuture = Http().newServerAt(host, port).bind(router)
}
