package com.stupidbird.controllers

import akka.http.scaladsl.model.headers.HttpCookie
import com.stupidbird.StupidbirdService.{dbSession, executionContext}
import com.stupidbird.utils.SessionService.createUserSession
import com.stupidbird.models._
import com.stupidbird.routers._
import scalikejdbc._

import scala.concurrent.Future
import java.util.UUID.randomUUID
import com.github.t3hnar.bcrypt._
import akka.http.scaladsl.server.Directives._
import com.stupidbird.utils.UserSession

object AuthenticationController {

  def register(request: RegisterRequest): Future[RegisterResponse] = {
    if (!doPasswordsMatch(request.password, request.passwordConfirm)) Future(RegisterResponse("password confirmation failed"))
    else for {
      hash <- hashUserPassword(request.password)
      maybeUserAdded <- createNewUser(request.email, hash)
    } yield RegisterResponse(maybeUserAdded)
  }

  def login(request: LoginRequest): Future[LoginResponse] = {
    for {
      maybeUser <- fetchUser(request.email)
      passwordIsCorrect <- isPasswordCorrect(request.password, maybeUser.getOrElse(null.asInstanceOf[User]).hash)
      userToken <- if (passwordIsCorrect) {
        val user = maybeUser.getOrElse(null.asInstanceOf[User])
        val userSession = UserSession(
          userId = user.id,
          accountId = "",
          blogId = "",
          sessionId = randomUUID.toString,
          email = user.email
        )
        createUserSession(userSession)
      } else Future("")
    } yield {
      LoginResponse(userToken)
    }
  }

  def logout() = ???

  private def createNewUser(email: String, hash: String)(implicit dbSession: DBSession): Future[String] = Future {
    val generatedId = randomUUID.toString
    val u = User.column
    withSQL {
      insert.into(User).namedValues(
        u.id -> generatedId,
        u.email -> email,
        u.hash -> hash
      )
    }.update.apply()
    generatedId
  }

  private def fetchUser(email: String): Future[Option[User]] = Future {
    val u = User.syntax("u")
    withSQL {
      select(u.result.id, u.result.email, u.result.hash)
        .from(User as u)
        .where.eq(u.email, email)
    }.map(rs => User(
      rs.string(u.resultName.id),
      rs.string(u.resultName.email),
      rs.string(u.resultName.hash)
    )).single.apply()
  }

  private def isPasswordCorrect(password: String, hash: String): Future[Boolean] = Future.fromTry(
    password.isBcryptedSafeBounded(hash)
  )

  private def createSession(userId: String): Future[Unit] = ???

  private def doPasswordsMatch(password: String, passwordConfirm: String): Boolean = password == passwordConfirm

  private def hashUserPassword(password: String): Future[String] = Future.fromTry(password.bcryptSafeBounded(12))

}
