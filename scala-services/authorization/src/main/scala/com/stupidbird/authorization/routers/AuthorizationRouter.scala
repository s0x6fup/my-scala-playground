package com.stupidbird.authorization.routers

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.stupidbird.utils.UserSession
import spray.json._
import com.stupidbird.authorization.controllers.AuthorizationController.isAuthorized

trait AuthorizationJsonProtocol extends DefaultJsonProtocol {
  implicit val isAuthorizedRequestFormat: RootJsonFormat[IsAuthorizedRequest] = jsonFormat1(IsAuthorizedRequest)
  implicit val isAuthorizedResponseFormat: RootJsonFormat[IsAuthorizedResponse] = jsonFormat1(IsAuthorizedResponse)
}

object AuthorizationRouter extends AuthorizationJsonProtocol with SprayJsonSupport {

  def apply()(implicit callScope: UserSession): Route = concat(
    path("authorization" / "is-authorized") {
      post {
        entity(as[IsAuthorizedRequest])(request => complete(isAuthorized(request)))
      }
    }
  )
}

case class IsAuthorizedRequest(
  permission: String
)

case class IsAuthorizedResponse(
  authorized: Boolean
)
