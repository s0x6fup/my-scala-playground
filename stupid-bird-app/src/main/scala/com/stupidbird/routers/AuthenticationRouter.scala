package com.stupidbird.routers

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.stupidbird.controllers.AuthenticationController._
import spray.json._

trait AuthorizationJsonProtocol extends DefaultJsonProtocol {
  implicit val registerRequestFormat = jsonFormat4(RegisterRequest)
  implicit val registerResponseFormat = jsonFormat1(RegisterResponse)
}

object AuthenticationRouter extends AuthorizationJsonProtocol with SprayJsonSupport {
  def apply(): Route = (path("register") & post) {
    entity(as[RegisterRequest]) {registerRequest => complete(register(registerRequest))}
  }
}

case class RegisterRequest(
                            email: String,
                            username: String,
                            password: String,
                            passwordConfirm: String
                          )

case class RegisterResponse(
                             status: String
                           )