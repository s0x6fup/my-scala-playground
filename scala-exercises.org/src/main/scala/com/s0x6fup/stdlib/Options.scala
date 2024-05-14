package com.s0x6fup

/*
 * check the tests file
 * */
object Options extends App {
  def maybeItWillReturnSomething(flag: Boolean): Option[String] = {
    if (flag) Some("Found value") else None
  }
}
