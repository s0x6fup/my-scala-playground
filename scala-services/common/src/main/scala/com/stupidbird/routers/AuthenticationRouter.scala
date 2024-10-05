package com.stupidbird.routers

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes.{NoContent, Unauthorized}
import akka.http.scaladsl.model.headers.HttpCookie
import com.stupidbird.services.AuthenticationService._
import com.stupidbird.utils.UserSession
import com.stupidbird.utils.AuthorizationClient.withAuthV2
import spray.json._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait AuthenticationJsonProtocol extends DefaultJsonProtocol {
  implicit val registerRequestFormat: RootJsonFormat[RegisterRequest] = jsonFormat3(RegisterRequest)
  implicit val registerResponseFormat: RootJsonFormat[RegisterResponse] = jsonFormat1(RegisterResponse)
  implicit val loginRequestFormat: RootJsonFormat[LoginRequest] = jsonFormat2(LoginRequest)
  implicit val loginResponseFormat: RootJsonFormat[LoginResponse] = jsonFormat1(LoginResponse)
  implicit val logoutRequestFormat: RootJsonFormat[LogoutRequest] = jsonFormat0(LogoutRequest)
  implicit val logoutResponseFormat: RootJsonFormat[LogoutResponse] = jsonFormat0(LogoutResponse)
  implicit val logoutAllRequestFormat: RootJsonFormat[LogoutAllRequest] = jsonFormat0(LogoutAllRequest)
  implicit val logoutAllResponseFormat: RootJsonFormat[LogoutAllResponse] = jsonFormat0(LogoutAllResponse)
  implicit val listAllSessionsRequestFormat: RootJsonFormat[ListAllSessionsRequest] = jsonFormat0(ListAllSessionsRequest)
  implicit val listAllSessionsResponseFormat: RootJsonFormat[ListAllSessionsResponse] = jsonFormat1(ListAllSessionsResponse)
}

object AuthenticationRouter extends AuthenticationJsonProtocol with SprayJsonSupport {
  def apply()(implicit callScope: UserSession, executionContext: ExecutionContext, system: ActorSystem[Any]): Route = concat(
    path("auth" / "register") {
      post {
        entity(as[RegisterRequest])(request => withAuthV2("authn.register", complete(register(request))))
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
        entity(as[LogoutRequest])(request => withAuthV2("authn.logout", complete(logout(request))))
      }
    },
    path("auth" / "logoutAll") {
      post {
        entity(as[LogoutAllRequest])(request => withAuthV2("authn.logoutAll", complete(logoutAll(request))))
      }
    },
    path("auth" / "listAllSessions") {
      get {
        withAuthV2("authn.listAllSessions", complete(listAllSessions(ListAllSessionsRequest())))
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
