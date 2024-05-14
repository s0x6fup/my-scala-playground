package com.s0x6fup.stdlib

object Greeting {
  def english = "Hi"

  def espanol = "Hola"
}

class Movie(val name: String, val year: Short)

object Movie {
  def academyAwardBestMoviesForYear(x: Short) = {
    // This is a match statement, more powerful than a Java switch statement!
    x match {
      case 1930 => Some(new Movie("All Quiet On the Western Front", 1930))
      case 1931 => Some(new Movie("Cimarron", 1931))
      case 1932 => Some(new Movie("Grand Hotel", 1932))
      case _    => None
    }
  }
}

class Person(
    val name: String,
    private val superheroName: String
) //The superhero name is private!

object Person {
  def showMeInnerSecret(x: Person) = x.superheroName
}
