package com.stupidbird.session.controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directive1
import com.stupidbird.StupidbirdService.{dbSession, executionContext}

import java.time.Clock
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim, JwtHeader, JwtOptions}

import scala.concurrent.Future
import java.util.UUID.randomUUID
import akka.http.scaladsl.server.Directives._
import com.stupidbird.utils.RolesConfig
import scalikejdbc._

import scala.util.{Failure, Success}
import spray.json._

case class UserSession(
                        userId: String,
                        sessionId: String,
                        role: String,
                        exp: Long,
                        token: String,
                      )

case class UserSessionJwtPayload(
                                  userId: String,
                                  sessionId: String,
                                  role: String,
                                  exp: Long,
                                )

trait SessionServiceJsonProtocol extends DefaultJsonProtocol {
  implicit val UserSessionJwtPayloadJsonFormat: RootJsonFormat[UserSessionJwtPayload] = jsonFormat4(
    UserSessionJwtPayload
  )
}

object SessionController extends SessionServiceJsonProtocol with SprayJsonSupport {
  val jwtSecret = "abae61da-1f87-4a4c-a2d9-d6ef2eb4a124" // tmp for development
  implicit val clock: Clock = Clock.systemUTC
  lazy val anonymousUserSession: UserSession = UserSession(
    userId = "00000000-0000-0000-0000-000000000000",
    sessionId = "00000000-0000-0000-0000-000000000000",
    role = RolesConfig.Anonymous,
    exp = 0,
    token = "Anonymous"
  )

  def createUserSession(userId: String, userRole: String): Future[String] = Future {
    val userSessionJwtPayload: UserSessionJwtPayload = UserSessionJwtPayload(
      userId = userId,
      sessionId = randomUUID.toString, // TODO: make sure unique
      role = userRole,
      exp = (System.currentTimeMillis + 14400000) / 1000 // 4 hours,
    )

    sql"insert into user_session (id, user_id) values (${userSessionJwtPayload.sessionId}, ${userSessionJwtPayload.userId})".update
      .apply()

    Jwt.encode(userSessionJwtPayload.toJson.toString, jwtSecret, JwtAlgorithm.HS256)
  }

  // TODO: also read from header if cookie is missing
  // TODO: move it to client
  // TODO: deprecate this
  def getUserSession: Directive1[UserSession] = optionalCookie("userSession").flatMap {
    // akka http provide: https://doc.akka.io/docs/akka-http/current/routing-dsl/directives/basic-directives/provide.html
    case Some(maybeUserSession) => provide(extractUserSessionFromJwt(maybeUserSession.value))
    // providing anonymous user when cookie is absent
    case None => provide(anonymousUserSession)
  }

  def invalidateUserSession(userSession: UserSession): Future[Int] =
    Future(
      sql"delete from user_session where id = ${userSession.sessionId} and user_id = ${userSession.userId}".update
        .apply()
    )

  def invalidateAllUserSessions(userSession: UserSession): Future[Int] =
    Future(sql"delete from user_session where user_id = ${userSession.userId}".update.apply())

  def getAllUserSessions(userSession: UserSession): Future[Seq[String]] = Future {
    val allUserSessionsIds: Seq[String] =
      sql"select id from user_session where user_id = ${userSession.userId}".map(rs => rs.string("id")).list.apply()
    allUserSessionsIds
  }

  def extractUserSessionFromJwt(userSessionJwt: String): UserSession =
    Jwt.decodeRaw(userSessionJwt, jwtSecret, Seq(JwtAlgorithm.HS256)) match {
      case Success(userSessionJson) => extractUserSessionFromJson(userSessionJson, userSessionJwt)
      case Failure(exception)       => anonymousUserSession
    }

  private def extractUserSessionFromJson(userSessionJson: String, userSessionJwt: String): UserSession = {
    val userSessionJwtPayload: UserSessionJwtPayload = userSessionJson.parseJson.convertTo[UserSessionJwtPayload]
    validateUserSessionNotLoggedOut(userSessionJwtPayload, userSessionJwt)
  }

  private def validateUserSessionNotLoggedOut(
                                               userSessionJwtPayload: UserSessionJwtPayload,
                                               userSessionJwt: String
                                             ): UserSession = {
    val doesExist: Option[Int] =
      sql"select 1 from user_session where id = ${userSessionJwtPayload.sessionId} and user_id = ${userSessionJwtPayload.userId} limit 1"
        .map(_.int(1))
        .single
        .apply()
    doesExist match {
      case Some(_) =>
        UserSession(
          userId = userSessionJwtPayload.userId,
          sessionId = userSessionJwtPayload.sessionId,
          role = userSessionJwtPayload.role,
          exp = userSessionJwtPayload.exp,
          token = userSessionJwt,
        )
      case None => anonymousUserSession
    }
  }
}
