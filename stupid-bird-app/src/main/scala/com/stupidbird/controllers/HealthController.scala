package com.stupidbird.controllers

object HealthController {
  def IsHealthy():String = "ok"

  //  import io.circe._
  //  import io.circe.generic.semiauto._
  //  import io.circe.syntax._
  //  implicit val healthEncoder: Encoder[Health] = deriveEncoder[Health]
  //  def apply() = complete(Health("ok").asJson.noSpaces)
}
