package com.stupidbird.utils

import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.{optionalCookie, optionalHeaderValueByName, provide}
import com.stupidbird.utils.SessionService.{anonymousUserSession, extractUserSessionFromJwt}

object SessionClient {

  def extractUserSession: Directive1[UserSession] = optionalCookie("userSession").flatMap {
    case Some(maybeUserSessionCookie) => provide(extractUserSessionFromJwt(maybeUserSessionCookie.value))
    case _ =>
      optionalHeaderValueByName("userSession").flatMap {
        case Some(maybeUserSessionJwt) => provide(extractUserSessionFromJwt(maybeUserSessionJwt))
        case _                         => provide(anonymousUserSession)
      }
  }

}
