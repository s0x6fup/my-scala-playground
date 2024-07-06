package com.stupidbird.services

import scala.concurrent.Future
import com.stupidbird.StupidbirdService.{dbSession, executionContext}
import com.stupidbird.utils.UserSession
import com.stupidbird.utils.RolesConfig._
import com.stupidbird.utils.PermissionsConfig

object AuthorizationService {
  /*
todo:
we want the service to fetch use the implicit session instead of passing role
*/
  def isAuthorized(permission: String)(implicit callScope: UserSession): Future[Boolean] = Future {
    lazy val permissionsMap = PermissionsConfig()
    println(s"[+] DEBUG: $callScope")
    val userRole = callScope.role
    permissionsMap(permission).contains(userRole)
  }
}
