package com.stupidbird.utils

import com.stupidbird.services.AuthorizationService._
import akka.http.scaladsl.model.StatusCodes.Unauthorized
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.StandardRoute
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object AuthorizationClient {

  def withAuth[A, B](permission: String, method: StandardRoute)(implicit callScope: UserSession): StandardRoute = {
    val authStatus = Await.result(isAuthorized(permission), 2.seconds)

    if (authStatus) method
    else complete(Unauthorized)
  }

  def withAuthV2[A, B](permission: String, method: StandardRoute)(implicit callScope: UserSession): StandardRoute = {
    val authorizationServerUrl = "http://localhost:9002/authorization/is-authorized"

    for {
      maybeAuthorized =
    }

    if (authStatus) method
    else complete(Unauthorized)
  }

}
