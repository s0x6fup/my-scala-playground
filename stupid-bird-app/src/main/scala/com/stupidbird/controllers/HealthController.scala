package com.stupidbird.controllers

import com.stupidbird.routers.HealthResponse
import com.stupidbird.utils.UserSession

object HealthController {
  def IsHealthy()(implicit callScope: UserSession): HealthResponse = {
    println(callScope)
    HealthResponse("ok")
  }

  //  import io.circe._
  //  import io.circe.generic.semiauto._
  //  import io.circe.syntax._
  //  implicit val healthEncoder: Encoder[Health] = deriveEncoder[Health]
  //  def apply() = complete(Health("ok").asJson.noSpaces)
}