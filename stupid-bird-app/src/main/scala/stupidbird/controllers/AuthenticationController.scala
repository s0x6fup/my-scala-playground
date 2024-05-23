package com.stupidbird.controllers

import akka.http.scaladsl.server.Directives._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._
import scalikejdbc._
import com.stupidbird.StupidbirdService.{dbSession, executionContext}
import scala.concurrent.Future
import scala.util.{Success, Failure}
import akka.http.scaladsl.server.Route

object AuthenticationController {

  case class Foo(bar: String)
  implicit val healthEncoder: Encoder[Foo] = deriveEncoder[Foo]

  def register() = {
    for {
      result <- addUserToDatabase
    } yield Foo(result).asJson.noSpaces
  }

  def login() = ???

  def logout() = ???

  private def addUserToDatabase(implicit
      dbSession: DBSession
  ): Future[String] = Future {
    val resultTest: List[Int] = withSQL {
      select(sqls"1")
    }.map(rs => rs.int(1)).list.apply()
    println(resultTest)
    "test"
  }
}
