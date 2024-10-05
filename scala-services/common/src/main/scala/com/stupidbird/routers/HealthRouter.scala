package com.stupidbird.routers

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes.Unauthorized
import com.stupidbird.services.HealthService._
import com.stupidbird.utils.UserSession
import com.stupidbird.StupidbirdService.{executionContext, system}
import com.stupidbird.utils.AuthorizationClient.{withAuth, withAuthV2}
import spray.json._
import scalikejdbc._

trait HealthJsonProtocol extends DefaultJsonProtocol {
  implicit val healthRequestFormat: RootJsonFormat[HealthRequest] = jsonFormat0(HealthRequest)
  implicit val healthResponseFormat: RootJsonFormat[HealthResponse] = jsonFormat1(HealthResponse)
}

object HealthRouter extends HealthJsonProtocol with SprayJsonSupport {

  def apply()(implicit callScope: UserSession): Route =
    (path("health") & get) {
      println(s"[+] DEBUG: $callScope")
      withAuthV2("health.read", complete(IsHealthy()))
    }
}

case class HealthRequest()

case class HealthResponse(
  status: String
)
