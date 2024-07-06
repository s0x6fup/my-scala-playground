package com.stupidbird.domains

import scalikejdbc._

case class Post(
                 id: String,
                 userId: String,
                 title: String,
                 body: String
               )

object Post extends SQLSyntaxSupport[Post] {
  def apply(p: ResultName[Post])(rs: WrappedResultSet): Post =
    new Post(
      rs.string(p.id),
      rs.string(p.userId),
      rs.string(p.title),
      rs.string(p.body)
    )

  def apply2(id: String, userId: String, title: String, body: String): Post = new Post(id, userId, title, body)
  
  def empty(): Post = new Post("", "", "", "")
}

