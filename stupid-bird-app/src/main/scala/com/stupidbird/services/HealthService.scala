package com.stupidbird.services

import com.stupidbird.routers.HealthResponse
import com.stupidbird.utils.UserSession

object HealthService {
  def IsHealthy()(implicit callScope: UserSession): HealthResponse = {
    HealthResponse("ok")
  }
}
