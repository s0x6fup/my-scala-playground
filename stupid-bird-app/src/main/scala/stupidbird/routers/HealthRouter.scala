package com.stupidbird.routers

import akka.http.scaladsl.server.{Route}
import akka.http.scaladsl.server.Directives._
import com.stupidbird.controllers.HealthController

object HealthRouter {
  def apply(): Route = { path("test") { get { HealthController() } } }
}
