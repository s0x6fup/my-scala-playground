package com.stupidbird.utils

import scalikejdbc._

object DatabaseInitializer {
  def apply(): Unit = {
    DB readOnly { implicit dbSession =>
      try {
        sql"select 1 from user limit 1".map(rs => rs.get[Int](1)).single()
      } catch {
        case e: java.sql.SQLException =>
          DB autoCommit { implicit dbSession =>
            sql"""create table user (
                   id varchar(255) not null primary key,
                   workspaceId varchar(255) not null,
                   entity varchar(255),
                   created_timestamp varchar(255),
                   updated_timestamp varchar(255),
                   unique (id)
                 );

                  create table workspace (
                   id varchar(255) not null primary key,
                   entity varchar(255),
                   created_timestamp varchar(255),
                   updated_timestamp varchar(255),
                   unique (id)
                 );

                  create table notebook (
                   id varchar(255) not null primary key,
                   tenantId varchar(255) not null,
                   entity varchar(255),
                   created_timestamp varchar(255),
                   updated_timestamp varchar(255),
                   unique (id)
                 );

                 create table note (
                   id varchar(255) not null primary key,
                   tenantId varchar(255) not null,
                   entity varchar(255),
                   created_timestamp varchar(255),
                   updated_timestamp varchar(255),
                   unique (id)
                 );
                 """.execute()
          }
      }
    }
    ()
  }
}
