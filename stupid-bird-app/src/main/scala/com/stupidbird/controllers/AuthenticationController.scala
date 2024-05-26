package com.stupidbird.controllers

import akka.http.scaladsl.server.Directives._
import scalikejdbc._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import akka.http.scaladsl.server.Route
import com.stupidbird.StupidbirdService.{dbSession, executionContext}
import com.stupidbird.models.User
import com.stupidbird.routers._

object AuthenticationController {
  def register(registerRequest: RegisterRequest): Future[RegisterResponse] = {
    for {
      validated <- validatePasswordsMatch(registerRequest.password, registerRequest.passwordConfirm)
      maybeUserAdded <- if (validated) addUserToDatabase(registerRequest.email, registerRequest.username, registerRequest.password)
      else Future("Password confirmation failed")
    } yield RegisterResponse(maybeUserAdded)
  }

  def login() = ???

  def logout() = ???

  private def addUserToDatabase(email: String, username: String, password: String)(implicit dbSession: DBSession): Future[String] = Future {
    // test function
    val resultTest: List[Int] = withSQL(select(sqls"1")).map(rs => rs.int(1)).list.apply()
    "User added successfully"
  }

  private def validatePasswordsMatch(password: String, passwordConfirm: String): Future[Boolean] =
    Future(password == passwordConfirm)
}
