package com.s0x6fup.stdlib

import org.scalatest._
import wordspec._
import matchers._
import com.s0x6fup.stdlib._
import java.util.Date
import com.s0x6fup.stdlib.HigherOrderFunctions.addWithoutSyntaxSugar
import scala.collection.immutable
import com.s0x6fup.stdlib.HigherOrderFunctions.fiveAdder

class StdlibTestsSpec extends AnyWordSpec with should.Matchers {
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
      val aClass = new Classes.ClassWithValParameter("Gandalf")
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

  "std lib objects" should {
    "best move of 1932" in {

      Movie.academyAwardBestMoviesForYear(1932).get.name should be(
        "Grand Hotel"
      )
    }

    "accessing private values using companion object" in {
      val clark = new Person("Clark Kent", "Superman")
      val peter = new Person("Peter Parker", "Spider-Man")

      Person.showMeInnerSecret(clark) should be("Superman")
      Person.showMeInnerSecret(peter) should be("Spider-Man")
    }
  }

  "std lib tuples" should {
    "simple tuple demonstration" in {
      val tuple = ("apple", "dog")
      val fruit = tuple._1
      val animal = tuple._2

      fruit should be("apple")
      animal should be("dog")
    }

    "tuples with mixed types" in {
      val tuple5 = ("a", 1, 2.2, new Date(), "five")

      tuple5._2 should be(1)
      tuple5._5 should be("five")
    }

    "multiple variables from a tuple" in {
      val student = ("Sean Rogers", 21, 3.5)
      val (name, age, gpa) = student

      name should be("Sean Rogers")
      age should be(21)
      gpa should be(3.5)
    }

    "swapping elements of tuple with 2 elements using swap method" in {
      val tuple = ("apple", 3).swap

      tuple._1 should be(3)
      tuple._2 should be("apple")
    }
  }

  "std lib higher order functions" should {
    "function literals" in {
      def lambda = { x: Int => x + 1 }
      def lambda2 = (x: Int) => x + 2
      val lambda3 = (x: Int) => x + 3

      val lambda4 = new Function1[Int, Int] {
        def apply(v1: Int): Int = v1 - 1
      }

      def lambda5(x: Int) = x + 1

      val result = lambda(3)
      val result1andhalf = lambda.apply(3)

      val result2 = lambda2(3)
      val result3 = lambda3(3)
      val result4 = lambda4(3)
      val result5 = lambda5(3)

      result should be(4)
      result1andhalf should be(4)
      result2 should be(5)
      result3 should be(6)
      result4 should be(2)
      result5 should be(4)
    }

    "An anonymous function can also take on a different look by taking out the brackets" in {
      def lambda = (x: Int) => x + 1
      def result = lambda(5)
      result should be(6)
    }

    "scala detects that i use variables outside of scope and creates an object instance to hold the variables" in {
      var incrementer = 1

      def closure = { x: Int => x + incrementer }

      val result1 = closure(10)
      result1 should be(
        11
      )

      incrementer = 2

      val result2 = closure(10)
      result2 should be(
        12
      )
    }

    "higher order functions (functions that get funcs as args or return them)" in {
      def summation(x: Int, y: Int => Int) = y(x)

      var incrementer = 3
      def closure = (x: Int) => x + incrementer

      val result = summation(10, closure)
      result should be(
        13
      )

      incrementer = 4
      val result2 = summation(10, closure)
      result2 should be(
        14
      )
    }

    "higher order functions returning functions" in {
      addWithoutSyntaxSugar(1).isInstanceOf[Function1[Int, Int]] should be(
        true
      ) // this proves that it is a function :)

      addWithoutSyntaxSugar(2)(3) should be(
        5
      )

      fiveAdder(5) should be(10)
    }

  }
}
