package com.stupidbird

import akka.http.scaladsl.server.{Route}
import akka.http.scaladsl.server.Directives._

object HealthRouter {
  def apply(): Route = { path("test") { get { HealthController() } } }
}
