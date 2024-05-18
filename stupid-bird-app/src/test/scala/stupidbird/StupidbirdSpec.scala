package com.stupidbird

import org.specs2.mutable.Specification
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.Specs2RouteTest
import akka.http.scaladsl.server._
import Directives._

class StupidbirdSpec extends Specification with Specs2RouteTest {

  "The service" should {

    "return a greeting for GET requests to the root path" in {
      Get("/test") ~> HealthRouter() ~> check {
        responseAs[String] shouldEqual "{\"health\":\"ok\"}"
      }
    }
  }
}
