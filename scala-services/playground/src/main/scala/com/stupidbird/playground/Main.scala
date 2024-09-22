package com.stupidbird.playground

import akka.actor.typed.ActorSystem
import com.stupidbird.playground.http._
import akka.actor.typed.scaladsl.Behaviors
import scala.concurrent.ExecutionContext

/**
 * this project is used to run different PoCs i am working on
 * that might or might not be implemented later
 */
object Main {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Any] = ActorSystem(
      guardianBehavior = Behaviors.empty,
      name = "playground-system"
    )

    implicit val executionContext: ExecutionContext = system.executionContext

    for {
      response <- MyHttpClient.sendPocPostRequest(url = "https://webhook.site/dfaea5e8-7bce-49c6-93e8-6f9cab3a8036")
    } yield println(response)

    /*
     * since there's async work (Future) we must keep the main thread alive long
     * enough for all Futures to complete
     */
    Thread.sleep(5000)
  }

}
