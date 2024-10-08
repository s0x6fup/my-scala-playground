package com.stupidbird.services

import akka.http.scaladsl.model.headers.HttpCookie
import com.stupidbird.StupidbirdService.{dbSession, executionContext}
import com.stupidbird.utils.SessionService._
import com.stupidbird.domains._
import com.stupidbird.routers
import com.stupidbird.routers._
import scalikejdbc._
import scala.concurrent.Future
import java.util.UUID.randomUUID
import com.github.t3hnar.bcrypt._
import akka.http.scaladsl.server.Directives._
import com.stupidbird.utils.UserSession
import com.stupidbird.utils.RolesConfig

object AuthenticationService {

  def register(request: RegisterRequest): Future[RegisterResponse] = {
    if (!doPasswordsMatch(request.password, request.passwordConfirm))
      Future(RegisterResponse("password confirmation failed"))
    else
      for {
        hash           <- hashUserPassword(request.password)
        maybeUserAdded <- createNewUser(request.email, hash)
      } yield RegisterResponse(maybeUserAdded)
  }

  def login(request: LoginRequest): Future[LoginResponse] = {
    for {
      maybeUser         <- fetchUser(request.email)
      passwordIsCorrect <- isPasswordCorrect(request.password, maybeUser.getOrElse(null.asInstanceOf[User]).hash)
      userToken <- if (passwordIsCorrect) {
        val user = maybeUser.getOrElse(null.asInstanceOf[User])
        createUserSession(userId = user.id, userRole = user.role)
      } else Future("")
    } yield {
      LoginResponse(userToken)
    }
  }

  def logout(request: LogoutRequest)(implicit callScope: UserSession): Future[LogoutResponse] = {
    for {
      _ <- invalidateUserSession(callScope)
    } yield LogoutResponse()
  }

  def logoutAll(request: LogoutAllRequest)(implicit callScope: UserSession): Future[LogoutAllResponse] = {
    for {
      _ <- invalidateAllUserSessions(callScope)
    } yield LogoutAllResponse()
  }

  def listAllSessions(
    request: ListAllSessionsRequest
  )(implicit callScope: UserSession): Future[ListAllSessionsResponse] = {
    for {
      allUserSessions <- getAllUserSessions(callScope)
    } yield ListAllSessionsResponse(allUserSessions)
  }

  private def createNewUser(email: String, hash: String)(implicit dbSession: DBSession): Future[String] = Future {
    val generatedId = randomUUID.toString
    val u = User.column
    withSQL {
      insert
        .into(User)
        .namedValues(
          u.id    -> generatedId,
          u.email -> email,
          u.hash  -> hash,
          u.role  -> RolesConfig.User
        )
    }.update.apply()
    generatedId
  }

  private def fetchUser(email: String): Future[Option[User]] = Future {
    val u = User.syntax("u")
    withSQL {
      select(u.result.id, u.result.email, u.result.hash, u.result.role)
        .from(User as u)
        .where
        .eq(u.email, email)
    }.map(
        rs =>
          User(
            rs.string(u.resultName.id),
            rs.string(u.resultName.email),
            rs.string(u.resultName.hash),
            rs.string(u.resultName.role)
        )
      )
      .single
      .apply()
  }

  private def isPasswordCorrect(password: String, hash: String): Future[Boolean] = Future.fromTry(
    password.isBcryptedSafeBounded(hash)
  )

  private def doPasswordsMatch(password: String, passwordConfirm: String): Boolean = password == passwordConfirm

  private def hashUserPassword(password: String): Future[String] = Future.fromTry(password.bcryptSafeBounded(12))

}
