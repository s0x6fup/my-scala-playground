package com.stupidbird.domains

import scalikejdbc._

case class Comment(
                 id: String,
                 userId: String,
                 postId: String,
                 body: String
               )

object Comment extends SQLSyntaxSupport[Comment] {
  def apply(p: ResultName[Comment])(rs: WrappedResultSet): Comment =
    new Comment(
      rs.string(p.id),
      rs.string(p.userId),
      rs.string(p.postId),
      rs.string(p.body)
    )

  def empty(): Comment = new Comment("", "", "", "")
}

