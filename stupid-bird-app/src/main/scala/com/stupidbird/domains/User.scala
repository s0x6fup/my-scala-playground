package com.stupidbird.domains

import scalikejdbc._

case class User(
                 id: String,
                 email: String,
                 hash: String,
                 role: String
               )

object User extends SQLSyntaxSupport[User] {
  def apply(u: ResultName[User])(rs: WrappedResultSet): User =
    new User(
      rs.string(u.id),
      rs.string(u.email),
      rs.string(u.hash),
      rs.string(u.role)
    )
}
