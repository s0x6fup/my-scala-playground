package com.stupidbird.routers

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes.{NoContent, Unauthorized}
import akka.http.scaladsl.model.headers.HttpCookie
import com.stupidbird.controllers.AuthenticationController._
import spray.json._

import scala.util.{Failure, Success}

trait AuthorizationJsonProtocol extends DefaultJsonProtocol {
  implicit val registerRequestFormat = jsonFormat3(RegisterRequest)
  implicit val registerResponseFormat = jsonFormat1(RegisterResponse)
  implicit val loginRequestFormat = jsonFormat2(LoginRequest)
  implicit val loginResponseFormat = jsonFormat1(LoginResponse)
}

object AuthenticationRouter extends AuthorizationJsonProtocol with SprayJsonSupport {
  def apply(): Route = concat(
    path("auth" / "register") {
      post {
        entity(as[RegisterRequest])(request => complete(register(request)))
      }
    },
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
    }
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