package com.stupidbird.authorization.controllers

import scala.concurrent.Future
import com.stupidbird.authorization.AuthorizationServer.executionContext
import com.stupidbird.authorization.PermissionsConfig
import com.stupidbird.authorization.routers.{IsAuthorizedRequest, IsAuthorizedResponse}
import com.stupidbird.utils.UserSession

object AuthorizationController {

  def isAuthorized(
    request: IsAuthorizedRequest
  )(implicit callScope: UserSession): IsAuthorizedResponse = {
    lazy val permissionsMap = PermissionsConfig()
    println(s"[+] DEBUG: $callScope")
    val userRole = callScope.role
    val authorized = permissionsMap(request.permission).contains(userRole)
    IsAuthorizedResponse(authorized)
  }

}
