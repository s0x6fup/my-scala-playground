package com.stupidbird.utils

import scalikejdbc._

object DatabaseInitializer {

  def run() {
    DB readOnly { implicit s =>
      try {
        sql"select 1 from users limit 1".map(_.long(1)).single.apply()
      } catch {
        case execution: java.sql.SQLException =>
          DB autoCommit { implicit s =>
            sql"""
create table users (
  id varchar(255) not null primary key,
  email varchar(255) not null,
  hash varchar(255) not null,
  entity varchar(255) not null,
  unique (id),
  unique (email),
);
   """.execute.apply()
          }
      }
    }
  }

}
