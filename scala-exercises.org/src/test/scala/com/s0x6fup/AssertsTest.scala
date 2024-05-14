package com.s0x6fup

import org.scalatest._
import flatspec._
import matchers._

class AssertsTestSpec extends AnyFlatSpec with should.Matchers {
  // some filler test to confirm that it compiles
  "my empty set" should "have exactly 0 items" in {
    assert(Set.empty.size == 0)
  }

  "this code" should "return true" in {
    true should be(true)
  }

  "booleans in asserts" should "return true" in {
    val v1 = 4
    v1 shouldEqual 4
  }

  "another test" should "return true" in {
    assert(2 == 1 + 1)
  }
}
