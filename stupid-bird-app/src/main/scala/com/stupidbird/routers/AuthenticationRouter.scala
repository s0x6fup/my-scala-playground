package com.stupidbird.routers

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes.{NoContent, Unauthorized}
import akka.http.scaladsl.model.headers.HttpCookie
import com.stupidbird.services.AuthenticationService._
import com.stupidbird.utils.UserSession
import com.stupidbird.utils.AuthorizationClient.withAuth
import spray.json._
import scala.util.{Failure, Success}

trait AuthorizationJsonProtocol extends DefaultJsonProtocol {
  implicit val registerRequestFormat = jsonFormat3(RegisterRequest)
  implicit val registerResponseFormat = jsonFormat1(RegisterResponse)
  implicit val loginRequestFormat = jsonFormat2(LoginRequest)
  implicit val loginResponseFormat = jsonFormat1(LoginResponse)
  implicit val logoutRequestFormat = jsonFormat0(LogoutRequest)
  implicit val logoutResponseFormat = jsonFormat0(LogoutResponse)
  implicit val logoutAllRequestFormat = jsonFormat0(LogoutAllRequest)
  implicit val logoutAllResponseFormat = jsonFormat0(LogoutAllResponse)
  implicit val listAllSessionsRequestFormat = jsonFormat0(ListAllSessionsRequest)
  implicit val listAllSessionsResponseFormat = jsonFormat1(ListAllSessionsResponse)
}

object AuthenticationRouter extends AuthorizationJsonProtocol with SprayJsonSupport {
  def apply()(implicit callScope: UserSession): Route = concat(
    path("auth" / "register") {
      post {
        entity(as[RegisterRequest])(request => withAuth("authn.register", complete(register(request))))
      }
    },
    // todo: move all logic to the controller so i can add a permissions here
    path("auth" / "login") {
      post {
        entity(as[LoginRequest])(request => onComplete(login(request)) {
          case Success(loginResponse) => {
            if (loginResponse.jwtToken.nonEmpty) setCookie(HttpCookie("userSession", value = loginResponse.jwtToken)) {
              complete(NoContent)
            }
            else complete(Unauthorized)
          }
          case Failure(error) => throw new Exception(error)
        })
      }
    },
    path("auth" / "logout") {
      post {
        entity(as[LogoutRequest])(request => withAuth("authn.logout", complete(logout(request))))
      }
    },
    path("auth" / "logoutAll") {
      post {
        entity(as[LogoutAllRequest])(request => withAuth("authn.logoutAll", complete(logoutAll(request))))
      }
    },
    path("auth" / "listAllSessions") {
      get {
        withAuth("authn.listAllSessions", complete(listAllSessions(ListAllSessionsRequest())))
      }
    },
    // archive user (soft delete)
    // list archived users (ONLY ADMIN)
    // delete user (ONLY ADMIN)
  )
}

case class RegisterRequest(
                            email: String,
                            password: String,
                            passwordConfirm: String
                          )

case class RegisterResponse(
                             id: String
                           )

case class LoginRequest(
                         email: String,
                         password: String
                       )

case class LoginResponse(
                          jwtToken: String
                        )

case class LogoutRequest(
                        )

case class LogoutResponse(
                         )

case class LogoutAllRequest(
                           )

case class LogoutAllResponse(
                            )

case class ListAllSessionsRequest(
                                 )

case class ListAllSessionsResponse(
                                  sessions: Seq[String]
                                  )
