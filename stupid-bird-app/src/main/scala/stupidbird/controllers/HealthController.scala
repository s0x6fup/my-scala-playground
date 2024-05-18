package com.stupidbird

import akka.http.scaladsl.server.Directives._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._

object HealthController {
  case class Health(health: String)
  implicit val healthEncoder: Encoder[Health] = deriveEncoder[Health]

  def apply() = complete(Health("ok").asJson.noSpaces)
}
