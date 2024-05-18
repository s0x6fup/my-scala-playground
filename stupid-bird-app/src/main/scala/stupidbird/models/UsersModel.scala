package com.stupidbird.models

import scalikejdbc._

case class User(
    email: String,
    username: String
)

object UsersModelInit {
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
