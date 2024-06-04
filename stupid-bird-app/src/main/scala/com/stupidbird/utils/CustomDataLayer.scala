package com.stupidbird.utils

import scalikejdbc._
import java.time.LocalDateTime
import scala.reflect.runtime.universe._

object CustomDataLayer {
  def create[T](model: T)(implicit dbSession: DBSession) = ???

  def getOne[T](model: T, id: String)(implicit dbSession: DBSession) = ???

}

case class cdlEntity(
                      id: String,
                      tenantId: String,
                      entity: String,
                      createdTimestamp: String,
                      updatedTimestamp: String
                    )

// TODO add a cdlEntity and then you can convert other entities to it and back
// required obj -> json in entity field of cdlEntity
// you will call the new object workspaceDomain and hardcode the table to workspace using override val tableName = "workspace"
