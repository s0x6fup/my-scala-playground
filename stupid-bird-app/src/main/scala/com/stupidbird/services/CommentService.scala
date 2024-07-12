package com.stupidbird.services

import com.stupidbird.StupidbirdService.{dbSession, executionContext}
import com.stupidbird.domains._
import com.stupidbird.routers._
import scalikejdbc._
import scala.concurrent.Future
import java.util.UUID.randomUUID
import com.stupidbird.utils.UserSession

object CommentService {

  def createComment(request: CreateCommentRequest)(implicit callScope: UserSession): Future[CreateCommentResponse] = {
    for {
      post <- PostService.getSinglePost(GetPostRequest(request.postId))
      maybeCommentId <- post match {
        case GetPostResponse(data) =>
          if (data.id != "") createNewComment(callScope.userId, request.postId, request.body)
          else Future("")
        case _ => Future("")
      }
    } yield CreateCommentResponse(maybeCommentId)
  }

  def getPostComments(request: GetPostCommentsRequest)(implicit callScope: UserSession): Future[GetPostCommentsResponse] = {
    for {
      // todo: no need to validate that post exists?
      maybeAllCommentsOnPost <- selectCommentsByPostId(request.postId)
    } yield GetPostCommentsResponse(maybeAllCommentsOnPost)
  }

  def getSingleComment(request: GetCommentRequest)(implicit callScope: UserSession): Future[GetCommentResponse] = {
    for {
      post <- selectCommentById(request.id)
    } yield GetCommentResponse(post.getOrElse(Comment.empty()))
  }

  def getMyComments(request: GetMyCommentsRequest)(implicit callScope: UserSession): Future[GetMyCommentsResponse] = {
    for {
      myComments <- selectCommentsByUserId(callScope.userId)
    } yield GetMyCommentsResponse(myComments)
  }

  def getUserComments(request: GetUserCommentsRequest)(implicit callScope: UserSession): Future[GetUserCommentsResponse] = {
    for {
      userComments <- selectCommentsByUserId(request.userId)
    } yield GetUserCommentsResponse(userComments)
  }

  def updateComment(request: UpdateCommentRequest)(implicit callScope: UserSession): Future[UpdateCommentResponse] = {
    for {
      status <- updateCommentById(request.id, callScope.userId,  request.body)
    } yield UpdateCommentResponse(
      if (status == 0) "Failed to update"
      else if (status == 1) "Successfully updated"
      else "Unexpected behavior"
    )
  }

  def deleteComment(request: DeleteCommentRequest)(implicit callScope: UserSession): Future[DeleteCommentResponse] = {
    for {
      status <- deleteCommentById(request.id)
    } yield DeleteCommentResponse(
      if (status == 0) "Failed to update"
      else if (status == 1) "Successfully updated"
      else "Unexpected behavior"
    )
  }

  private def createNewComment(userId: String, postId: String, body: String)(implicit dbSession: DBSession): Future[String] = Future {
    val generatedId = randomUUID.toString
    val c = Comment.column
    withSQL {
      insert.into(Comment).namedValues(
        c.id -> generatedId,
        c.userId -> userId,
        c.postId -> postId,
        c.body -> body
      )
    }.update.apply()
    generatedId
  }

  private def selectCommentsByPostId(postId: String)(implicit dbSession: DBSession): Future[List[Comment]] = Future {
    val c = Comment.syntax("c")
    withSQL {
      select(c.result.id, c.result.userId, c.result.postId, c.result.body)
        .from(Comment as c).where.eq(c.postId, postId)
    }.map(rs => Comment(
      rs.string(c.resultName.id),
      rs.string(c.resultName.userId),
      rs.string(c.resultName.postId),
      rs.string(c.resultName.body)
    )).list.apply()
  }

  private def selectCommentById(id: String)(implicit dbSession: DBSession): Future[Option[Comment]] = Future {
    val c = Comment.syntax("c")
    withSQL {
      select(c.result.id, c.result.userId, c.result.postId, c.result.body)
        .from(Comment as c)
        .where.eq(c.id, id)
    }.map(rs => Comment(
      rs.string(c.resultName.id),
      rs.string(c.resultName.userId),
      rs.string(c.resultName.postId),
      rs.string(c.resultName.body)
    )).single.apply()
  }

  private def selectCommentsByUserId(userId: String)(implicit dbSession: DBSession): Future[List[Comment]] = Future {
    val c = Comment.syntax("c")
    withSQL {
      select(c.result.id, c.result.userId, c.result.postId, c.result.body)
        .from(Comment as c)
        .where.eq(c.userId, userId)
    }.map(rs => Comment(
      rs.string(c.resultName.id),
      rs.string(c.resultName.userId),
      rs.string(c.resultName.postId),
      rs.string(c.resultName.body)
    )).list.apply()
  }

  private def updateCommentById(id: String, userId: String, body: String)(implicit dbSession: DBSession): Future[Int] = Future {
    val c = Comment.column
    withSQL {
      update(Comment).set(
        c.body -> body
      ).where.eq(c.id, id).and.eq(c.userId, userId) // prevent IDORs on update, todo: have this done implicitly
    }.update.apply()
  }

  // dangerous: should only be done by admin
  private def deleteCommentById(id: String)(implicit dbSession: DBSession): Future[Int] = Future {
    val c = Comment.column
    withSQL {
      delete.from(Comment).where.eq(c.id, id)
    }.update.apply()
  }

}
