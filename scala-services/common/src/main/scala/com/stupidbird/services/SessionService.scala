package com.stupidbird.utils

import akka.http.scaladsl.server.Directive1
import com.stupidbird.domains.User
import com.stupidbird.StupidbirdService.{dbSession, executionContext}
import java.time.Clock
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim, JwtHeader, JwtOptions}
import scala.concurrent.Future
import java.util.UUID.randomUUID
import akka.http.scaladsl.server.Directives._
import com.stupidbird.utils
import scalikejdbc._
import scala.util.{Failure, Success}
import spray.json._
import com.stupidbird.utils.RolesConfig

case class UserSession(
                        userId: String,
                        sessionId: String,
                        role: String,
                        exp: Long
                      )

object SessionService extends DefaultJsonProtocol {
  val jwtSecret = randomUUID.toString
  implicit val clock: Clock = Clock.systemUTC
  implicit val userSessionJsonFormat: RootJsonFormat[UserSession] = jsonFormat4(UserSession)
  lazy val anonymousUserSession: UserSession = UserSession(
    userId = "00000000-0000-0000-0000-000000000000",
    sessionId = "00000000-0000-0000-0000-000000000000",
    role = RolesConfig.Anonymous,
    exp = 0
  )

  def createUserSession(userSession: UserSession): Future[String] = Future {
    sql"insert into user_session (id, user_id) values (${userSession.sessionId}, ${userSession.userId})".update.apply()
    Jwt.encode(userSession.toJson.toString, jwtSecret, JwtAlgorithm.HS256)
  }

  def getUserSession: Directive1[UserSession] = optionalCookie("userSession").flatMap {
    // akka http provide: https://doc.akka.io/docs/akka-http/current/routing-dsl/directives/basic-directives/provide.html
     case Some(maybeUserSession) => provide(extractUserSessionFromJwt(maybeUserSession.value))
    // providing anonymous user when cookie is absent
     case None => provide(anonymousUserSession)
  }

  def invalidateUserSession(userSession: UserSession): Future[Int] =
    Future(sql"delete from user_session where id = ${userSession.sessionId} and user_id = ${userSession.userId}".update.apply())

  def invalidateAllUserSessions(userSession: UserSession): Future[Int] =
    Future(sql"delete from user_session where user_id = ${userSession.userId}".update.apply())

  def getAllUserSessions(userSession: UserSession): Future[Seq[String]] = Future {
    val allUserSessionsIds: Seq[String] = sql"select id from user_session where user_id = ${userSession.userId}".map(rs => rs.string("id")).list.apply()
    allUserSessionsIds
  }

  private def extractUserSessionFromJwt(userSessionJwt: String): UserSession =
    Jwt.decodeRaw(userSessionJwt, jwtSecret, Seq(JwtAlgorithm.HS256)) match {
      case Success(userSessionJson) => extractUserSessionFromJson(userSessionJson)
      case Failure(exception) => anonymousUserSession
    }

  private def extractUserSessionFromJson(userSessionJson: String): UserSession = {
    val userSession: UserSession = userSessionJson.parseJson.convertTo[UserSession]
    validateUserSessionNotLoggedOut(userSession)
  }

  private def validateUserSessionNotLoggedOut(userSession: UserSession): UserSession = {
    val doesExist: Option[Int] = sql"select 1 from user_session where id = ${userSession.sessionId} and user_id = ${userSession.userId} limit 1".map(_.int(1)).single.apply()
    doesExist match {
      case Some(_) => userSession
      case None => anonymousUserSession
    }
  }
}
