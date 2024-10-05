package com.stupidbird.utils

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.Unauthorized
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.StandardRoute
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse}

import scala.concurrent.{Await, ExecutionContext, Future}
import spray.json._

import scala.concurrent.duration.DurationInt

case class WithAuthV2Request(
  permission: String
)

case class WithAuthV2Response(
  authorized: Boolean
)

trait AuthorizationClientJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val WithAuthV2RequestJsonFormat: RootJsonFormat[WithAuthV2Request] = jsonFormat1(WithAuthV2Request)
  implicit val WithAuthV2ResponseJsonFormat: RootJsonFormat[WithAuthV2Response] = jsonFormat1(WithAuthV2Response)
}

object AuthorizationClient extends AuthorizationClientJsonProtocol {

  @deprecated(
    "No longer in use since splitting into services, always returns false"
  )
  def withAuth[A, B](permission: String, method: StandardRoute)(
    implicit
    callScope: UserSession
  ): StandardRoute = {

    complete(Unauthorized)
  }

  // https://doc.akka.io/docs/akka-http/10.0/client-side/request-level.html
  // https://www.youtube.com/watch?v=Agze0Ule5_0
  def withAuthV2(permission: String, method: StandardRoute)(
    implicit
    callScope: UserSession,
    system: ActorSystem[Any],
    executionContext: ExecutionContext
  ): StandardRoute = {

    val authorizationServerUrl =
      "http://localhost:9002/authorization/isAuthorized"
//      "https://webhook.site/d88c7508-fbc3-4af8-9a5b-667246455eba"

    val withAuthV2Request = WithAuthV2Request(
      permission = permission
    ).toJson.toString

    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(
        HttpRequest(
          method = HttpMethods.POST,
          uri = authorizationServerUrl,
          entity = HttpEntity(
            ContentTypes.`application/json`,
            withAuthV2Request
          )
        ).withHeaders(
          RawHeader("userSession", callScope.token)
        )
      )

    val entityFuture: Future[String] = responseFuture.flatMap(_.entity.toStrict(2 seconds)).map(_.data.utf8String)
    val withAuthV2Response = Await.result(entityFuture, 2 seconds).parseJson.convertTo[WithAuthV2Response]

    withAuthV2Response match {
      case WithAuthV2Response(true) => method
      case _                        => complete(Unauthorized)
    }

  }

}
