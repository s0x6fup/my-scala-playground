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
}
