package com.stupidbird.routers

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.stupidbird.controllers.AuthenticationController._

case class RegisterReqiest(
    email: String,
    username: String,
    password: String,
    passwordConfirm: String
)

// trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
//   implicit val itemFormat: RootJsonFormat[RegisterReqiest] = jsonFormat2(
//     RegisterReqiest.apply
//   )
// }

object AuthenticationRouter {
  def apply(): Route = (path("register") & post)(complete(register()))
}
