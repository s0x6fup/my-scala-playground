package com.stupidbird.utils

import com.stupidbird.services.AuthorizationService._
import com.stupidbird.StupidbirdService.{dbSession, executionContext}
import akka.http.scaladsl.model.StatusCodes.Unauthorized
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.StandardRoute
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Success, Failure}
import com.stupidbird.utils.UserSession

object AuthorizationClient {
  def withAuth[A, B](permission: String, method: StandardRoute)(implicit callScope: UserSession): StandardRoute = {
    val authStatus = Await.result(isAuthorized(permission), 2 seconds)

    if (authStatus) method
    else complete(Unauthorized)
  }
}
