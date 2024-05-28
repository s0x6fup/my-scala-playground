package com.stupidbird.routers

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.stupidbird.controllers.AuthenticationController._
import spray.json._

trait AuthorizationJsonProtocol extends DefaultJsonProtocol {
  implicit val registerRequestFormat = jsonFormat3(RegisterRequest)
  implicit val registerResponseFormat = jsonFormat1(RegisterResponse)
  implicit val loginRequestFormat = jsonFormat2(LoginRequest)
  implicit val loginResponseFormat = jsonFormat1(LoginResponse)
}

object AuthenticationRouter extends AuthorizationJsonProtocol with SprayJsonSupport {
  def apply(): Route = concat(
    path("register") {
      post {
        entity(as[RegisterRequest])(request => complete(register(request)))
      }
    },
    path("login") {
      post {
        entity(as[LoginRequest])(request => complete(login(request)))
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
                          id: String
                        )