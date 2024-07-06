package com.stupidbird.utils

import scalikejdbc._

object DatabaseInitializer {
  def init(): Unit = {
    DB readOnly { implicit dbSession =>
      try {
        sql"select 1 from user limit 1".map(rs => rs.get[Int](1)).single()
      } catch {
        case e: java.sql.SQLException =>
          DB autoCommit { implicit dbSession =>
            sql"""create table user (
                    id varchar(255) not null unique primary key,
                    email varchar(255) not null unique,
                    hash varchar(255) not null,
                    role varchar(255) not null
                  );""".execute.apply()
          }

          DB autoCommit { implicit dbSession =>
            sql"""create table user_session (
                    id varchar(255) not null unique primary key,
                    user_id varchar(255) not null
                  );""".execute.apply()
          }

          DB autoCommit { implicit dbSession =>
            sql"""create table post (
                    id varchar(255) not null unique primary key,
                    user_id varchar(255) not null,
                    title varchar(255) not null,
                    body varchar(255) not null,
                    archived int default 0
                  );""".execute.apply()
          }

          DB autoCommit { implicit dbSession =>
            sql"""create table comment (
                    id varchar(255) not null unique primary key,
                    user_id varchar(255) not null,
                    post_id varchar(255) not null,
                    body varchar(255) not null,
                    archived int default 0
                  );""".execute.apply()
          }
      }
    }
    ()
  }
}

