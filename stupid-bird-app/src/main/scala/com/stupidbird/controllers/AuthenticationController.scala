package com.stupidbird.controllers

import scalikejdbc._

import scala.concurrent.Future
import com.stupidbird.StupidbirdService.{dbSession, executionContext}
import com.stupidbird.models.User
import com.stupidbird.routers._
import java.util.UUID.randomUUID
import com.github.t3hnar.bcrypt._
import scala.util.Try

object AuthenticationController {

  def register(registerRequest: RegisterRequest): Future[RegisterResponse] = {
    if (!doPasswordsMatch(registerRequest.password, registerRequest.passwordConfirm)) Future(RegisterResponse("password confirmation failed"))
    else for {
      hash <- Future.fromTry(hashUserPassword(registerRequest.password))
      maybeUserAdded <- addUserToDatabase(registerRequest.email, hash)
    } yield RegisterResponse(maybeUserAdded)
  }

  def login() = ???

  def logout() = ???

  private def hashUserPassword(password: String): Try[String] = password.bcryptSafeBounded(12)

  private def addUserToDatabase(email: String, hash: String)(implicit dbSession: DBSession): Future[String] = Future {
    val generatedId = randomUUID.toString
    val userColumn = User.column
    withSQL {
      insert.into(User).namedValues(
        userColumn.id -> generatedId,
        userColumn.email -> email,
        userColumn.hash -> hash
      )
    }.update.apply()
    "registration complete"
  }

  private def doPasswordsMatch(password: String, passwordConfirm: String): Boolean =
    password == passwordConfirm
}
