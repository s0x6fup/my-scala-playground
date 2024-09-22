package com.stupidbird.services

import scala.concurrent.Future
import com.stupidbird.StupidbirdService.executionContext
import com.stupidbird.utils.UserSession

object AuthorizationService {
  /*
todo:
we want the service to fetch use the implicit session instead of passing role
*/
  def isAuthorized(permission: String)(implicit callScope: UserSession): Future[Boolean] = Future {
    lazy val permissionsMap = Map("" -> Seq("asdfasdfasdfasdf"))
    println(s"[+] DEBUG: $callScope")
    val userRole = callScope.role
    permissionsMap(permission).contains(userRole)
  }
}
