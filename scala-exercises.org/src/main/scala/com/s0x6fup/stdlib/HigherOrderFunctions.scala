package com.s0x6fup.stdlib

object HigherOrderFunctions {
  def addWithoutSyntaxSugar(x: Int): Function1[Int, Int] = {
    new Function1[Int, Int]() {
      def apply(y: Int): Int = x + y
    }
  }

  def fiveAdder: Function1[Int, Int] = addWithoutSyntaxSugar(5)
}
