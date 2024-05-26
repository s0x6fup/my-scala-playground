package com.stupidbird.utils

import scalikejdbc._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._
import java.time.LocalDateTime
import scala.reflect.runtime.universe._

object CustomDataLayer {
  def create[T](model: T)(implicit dbSession: DBSession) = ???

  def getOne[T](model: T, id: String)(implicit dbSession: DBSession) = ???

  def initTable[T: TypeTag](): Unit = {
    val tableName = typeOf[T].toString
    DB readOnly { implicit dbSession =>
      try {
        sql"select 1 from ${tableName} limit 1".map(rs => rs.get[Int](1)).single()
      } catch {
        case e: java.sql.SQLException =>
          DB autoCommit { implicit dbSession =>
            sql"""create table asdf (
                   id varchar(255) not null primary key,
                   workspaceId varchar(255) not null,
                   entity varchar(255),
                   created_timestamp varchar(255),
                   updated_timestamp varchar(255),
                   unique (id)
                 );""".execute()
          }
      }
    }
    ()
  }
}
