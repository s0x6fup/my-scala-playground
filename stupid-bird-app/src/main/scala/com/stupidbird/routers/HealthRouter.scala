package com.stupidbird.routers

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.stupidbird.controllers.HealthController._
import spray.json._

trait HealthJsonProtocol extends DefaultJsonProtocol {
  implicit val healthRequestFormat: RootJsonFormat[HealthRequest] = jsonFormat0(HealthRequest)
  implicit val healthResponseFormat: RootJsonFormat[HealthResponse] = jsonFormat1(HealthResponse)
}

object HealthRouter extends HealthJsonProtocol with SprayJsonSupport {
  def apply(): Route = (path("ishealthy") & get) {
    complete(HealthResponse(IsHealthy()))
  }
}

case class HealthRequest()

case class HealthResponse(
                           status: String
                         )