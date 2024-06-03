package com.stupidbird.utils

import akka.http.scaladsl.server.Directive1
import com.stupidbird.models.User
import com.stupidbird.StupidbirdService.{dbSession, executionContext}
import java.time.Clock
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim, JwtHeader, JwtOptions}
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._
import scala.concurrent.Future
import java.util.UUID.randomUUID
import akka.http.scaladsl.server.Directives._
import scalikejdbc._
import scala.util.{Failure, Success}

case class UserSession(
                        userId: String,
                        accountId: String,
                        blogId: String,
                        sessionId: String,
                        email: String
                      )

object UserSession {
  lazy val anonymous: UserSession = new UserSession(
    userId = "00000000-0000-0000-0000-000000000000",
    accountId = "00000000-0000-0000-0000-000000000000",
    blogId = "00000000-0000-0000-0000-000000000000",
    sessionId = "",
    email = ""
  )
}

object SessionService {
  val jwtSecret = randomUUID.toString
  implicit val clock: Clock = Clock.systemUTC
  implicit val userSessionEncoder: Encoder[UserSession] = deriveEncoder[UserSession]
  implicit val userSessionDecoder: Decoder[UserSession] = deriveDecoder[UserSession]


  def createUserSession(userSession: UserSession): Future[String] = Future {
    sql"insert into user_session (id) values (${userSession.sessionId})".update.apply()
    val userSessionAsJson = userSession.asJson.noSpaces
    val userToken = Jwt.encode(userSessionAsJson, jwtSecret, JwtAlgorithm.HS256)
    userToken
  }

  def getUserSession: Directive1[UserSession] = optionalCookie("userSession").flatMap {
    // akka http provide: https://doc.akka.io/docs/akka-http/current/routing-dsl/directives/basic-directives/provide.html
    case Some(maybeUserSession) => provide(extractUserSessionFromJwt(maybeUserSession.value))
    // providing anonymous user when cookie is absent
    case None => provide(UserSession.anonymous)
  }

  def invalidateUserSession(userSession: UserSession) =
    Future(sql"delete from user_session where id = ${userSession.sessionId}".update.apply())


  private def extractUserSessionFromJwt(userSessionJwt: String): UserSession =
    Jwt.decodeRaw(userSessionJwt, jwtSecret, Seq(JwtAlgorithm.HS256)) match {
      case Success(userSessionJson) => extractUserSessionFromJson(userSessionJson)
      case Failure(exception) => UserSession.anonymous
    }

  private def extractUserSessionFromJson(userSessionJson: String): UserSession =
    parser.parse(userSessionJson).flatMap(_.as[UserSession]) match {
      case Right(userSession) => validateUserSessionNotLoggedOut(userSession)
      case _ => UserSession.anonymous
    }

  private def validateUserSessionNotLoggedOut(userSession: UserSession): UserSession = {
    val doesExist: Option[Int] = sql"select 1 from user_session where id = ${userSession.sessionId} limit 1".map(_.int(1)).single.apply()
    doesExist match {
      case Some(_) => userSession
      case None => UserSession.anonymous
    }
  }
}
