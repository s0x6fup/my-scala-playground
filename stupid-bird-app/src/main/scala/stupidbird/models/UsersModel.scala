package com.stupidbird.models

import scalikejdbc._

case class User(
    id: String,
    email: String,
    username: String,
    profilePictureUrl: String,
    bio: Option[String]
)

/*
object User {
  def apply()(implicit test: String): Unit = {
    sql"""
create table members (
  id serial not null primary key,
  name varchar(64),
  created_at timestamp not null
)
""".execute.apply()
  }
}
 */
