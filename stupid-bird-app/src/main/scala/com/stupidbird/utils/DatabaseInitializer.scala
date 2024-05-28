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
                    hash varchar(255) not null
                  );""".execute.apply()

            /*
create table workspace (
   id varchar(255) not null unique,
   tenant_id varchar(255) not null,
   entity varchar(255),
   created_timestamp varchar(255),
   updated_timestamp varchar(255)
);

create table collection (
  id varchar(255) not null unique,
  tenant_id varchar(255) not null,
  entity varchar(255),
  created_timestamp varchar(255),
  updated_timestamp varchar(255)
);
""".execute()*/
          }
      }
    }
    ()
  }
}
