package com.s0x6fup.stdlib

/*
 * check the tests file
 * */
object Options {
  def maybeItWillReturnSomething(flag: Boolean): Option[String] = {
    if (flag) Some("Found value") else None
  }
}
