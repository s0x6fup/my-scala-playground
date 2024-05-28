package com.stupidbird.models

import scalikejdbc._

case class User(
                 id: String,
                 email: String,
                 hash: String
               )

object User extends SQLSyntaxSupport[User] {
  //  def apply(u: ResultName[User])(rs: WrappedResultSet): User = {
  def apply(u: ResultName[User])(rs: WrappedResultSet): User = {
    new User(
      rs.string(u.id),
      rs.string(u.email),
      rs.string(u.hash)
    )
  }
}
