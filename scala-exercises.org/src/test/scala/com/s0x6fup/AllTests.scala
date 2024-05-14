package com.s0x6fup

import org.scalatest._
import wordspec._
import matchers._
import com.s0x6fup.Classes.ClassWithValParameter

class AllTestsSpec extends AnyWordSpec with should.Matchers {
  // some filler test to confirm that it compiles
  "std lib asserts" should {
    "true is always true" in {
      true should be(true)
    }

    "4 is always 4" in {
      val v1 = 4
      v1 shouldEqual 4
    }

    "simple math works" in {
      assert(2 == 1 + 1)
    }
  }

  "std lib classes" should {
    "true is always true" in {
      val aClass = new ClassWithValParameter("Gandalf")
      aClass.name should be("Gandalf")
    }
  }

  "std lib options" should {
    "first section is correct" in {
      val someValue: Option[String] = Some("I am wrapped in something")
      someValue should be(Some("I am wrapped in something"))
      val emptyValue: Option[String] = None
      emptyValue should be(None)
    }

    "all getOrElse code implementation works" in {
      val value1 = Options.maybeItWillReturnSomething(true)
      val value2 = Options.maybeItWillReturnSomething(false)

      value1 getOrElse "No value" should be("Found value")
      value2 getOrElse "No value" should be("No value")
      value2 getOrElse {
        "default function"
      } should be("default function")
    }

    "checking whether option has value" in {
      val value1 = Options.maybeItWillReturnSomething(true)
      val value2 = Options.maybeItWillReturnSomething(false)

      value1.isEmpty should be(false)
      value2.isEmpty should be(true)
    }

    "option pattern matching" in {
      val someValue: Option[Double] = Some(20.0)
      val value = someValue match {
        case Some(v) => v
        case None    => 0.0
      }
      value should be(20.0)

      val noValue: Option[Double] = None
      val value1 = noValue match {
        case Some(v) => v
        case None    => 0.0
      }
      value1 should be(0.0)
    }

    "option as a collection using map" in {
      val number: Option[Int] = Some(3)
      val noNumber: Option[Int] = None
      val result1 = number.map(_ * 1.5)
      val result2 = noNumber.map(_ * 1.5)

      result1 should be(Some(4.5))
      result2 should be(None)
    }

    "option as a collection using fold" in {
      val number: Option[Int] = Some(3)
      val noNumber: Option[Int] = None
      val result1 = number.fold(1)(_ * 3)
      val result2 = noNumber.fold(1)(_ * 3)

      result1 should be(9)
      result2 should be(1)
    }
  }
}
