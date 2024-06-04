package com.stupidbird.controllers

import com.stupidbird.routers.HealthResponse
import com.stupidbird.utils.UserSession

object HealthController {
  def IsHealthy()(implicit callScope: UserSession): HealthResponse = {
    println(callScope)
    HealthResponse("ok")
  }
}