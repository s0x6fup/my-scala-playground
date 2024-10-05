package com.stupidbird.authorization

import org.specs2.mutable.Specification
import com.stupidbird.authorization.routers.{IsAuthorizedRequest, IsAuthorizedResponse}
import com.stupidbird.utils.SessionService.anonymousUserSession
import com.stupidbird.utils.{RolesConfig, UserSession}
import com.stupidbird.authorization.controllers.AuthorizationController.isAuthorized

class AuthorizationSpec extends Specification {

  "Authorization Server" should {

    "isAuthorized true" in {

      implicit val callScope = UserSession(
        userId = java.util.UUID.randomUUID.toString,
        sessionId = java.util.UUID.randomUUID.toString,
        role = RolesConfig.User,
        exp = 999999999
      )

      isAuthorized(IsAuthorizedRequest("test.user")) shouldEqual IsAuthorizedResponse(true)
    }

    "isAuthorized false" in {
      implicit val callScope = anonymousUserSession

      isAuthorized(IsAuthorizedRequest("test.user")) shouldEqual IsAuthorizedResponse(false)
    }

  }

}
