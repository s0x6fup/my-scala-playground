package com.stupidbird.playground.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import akka.stream.ActorMaterializer
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import spray.json._

/*
 * a simple client to send HTTP requests and consume HTTP responses.
 *
 * https://blog.rockthejvm.com/a-5-minute-akka-http-client/
 */
object MyHttpClient extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  import system.dispatcher

  def sendPocPostRequest(url: String): Future[String] = {
    val body = Map(
      "test1" -> "test1",
      "test2" -> "test2"
    ).toJson.toString

    val responseFuture /* is a future similar to JS promise */ = Http().singleRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = url,
        /*
         * https://doc.akka.io/docs/akka-http/current/common/json-support.html
         * creating a JSON body
         */
        entity = HttpEntity(
          ContentTypes.`application/json`,
          body
        )
      )
    )

    /*
     * handling responses can be found here
     * https://doc.akka.io/docs/akka-http/current/implications-of-streaming-http-entity.html
     * https://doc.akka.io/japi/akka-http/10.1/akka/http/scaladsl/model/ResponseEntity.html
     */
    responseFuture
      .flatMap(_.entity.toStrict(2.seconds))
      .map(_.data.utf8String)
  }

}
