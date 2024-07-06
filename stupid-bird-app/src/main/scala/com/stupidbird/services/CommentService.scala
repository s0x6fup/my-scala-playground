package com.stupidbird.services

import com.stupidbird.StupidbirdService.{dbSession, executionContext}
import com.stupidbird.utils.SessionService._
import com.stupidbird.domains._
import com.stupidbird.routers._
import scalikejdbc._
import scala.concurrent.Future
import java.util.UUID.randomUUID
import com.stupidbird.utils.UserSession
import com.stupidbird.services.PostService

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

  //  def getAllComments(request: GetAllCommentsRequest)(implicit callScope: UserSession): Future[GetAllCommentsResponse] = {
  //    for {
  //      allComments <- fetchAllComments()
  //    } yield GetAllCommentsResponse(allComments)
  //  }
  //
  //  def getSingleComment(request: GetCommentRequest)(implicit callScope: UserSession): Future[GetCommentResponse] = {
  //    for {
  //      post <- fetchCommentById(request.id)
  //    } yield GetCommentResponse(post.getOrElse(Comment.empty()))
  //  }
  //
  //  def getMyComments(request: GetMyCommentsRequest)(implicit callScope: UserSession): Future[GetMyCommentsResponse] = {
  //    for {
  //      myComments <- fetchCommentsByUserId(callScope.userId)
  //    } yield GetMyCommentsResponse(myComments)
  //  }
  //
  //  def getUserComments(request: GetUserCommentsRequest)(implicit callScope: UserSession): Future[GetUserCommentsResponse] = {
  //    for {
  //      userComments <- fetchCommentsByUserId(callScope.userId)
  //    } yield GetUserCommentsResponse(userComments)
  //  }
  //
  //  def updateComment(request: UpdateCommentRequest)(implicit callScope: UserSession): Future[UpdateCommentResponse] = {
  //    for {
  //      status <- updateUserComment(request.id, callScope.userId, request.postId, request.body)
  //    } yield UpdateCommentResponse(
  //        if (status == 0) "Failed to update"
  //        else if (status == 1) "Successfully updated"
  //        else "Unexpected behavior"
  //    )
  //  }
  //
  //  def deleteComment(request: DeleteCommentRequest)(implicit callScope: UserSession): Future[DeleteCommentResponse] = {
  //    for {
  //      status <- deleteUserComment(request.id)
  //    } yield DeleteCommentResponse(
  //        if (status == 0) "Failed to update"
  //        else if (status == 1) "Successfully updated"
  //        else "Unexpected behavior"
  //    )
  //  }

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

//  private def fetchAllComments()(implicit dbSession: DBSession): Future[List[Comment]] = Future {
//    val c = Comment.syntax("c")
//    withSQL {
//      select(c.result.id, c.result.userId, c.result.postId, c.result.body)
//        .from(Comment as c) // i can have IF statement here for optional Id to fetch either one or many posts (prone to issues?)
//    }.map(rs => Comment(
//      rs.string(c.resultName.id),
//      rs.string(c.resultName.userId),
//      rs.string(c.resultName.postId),
//      rs.string(c.resultName.body)
//    )).list.apply()
//  }
//
//  private def fetchCommentById(id: String)(implicit dbSession: DBSession): Future[Option[Comment]] = Future {
//    val c = Comment.syntax("c")
//    withSQL {
//      select(c.result.id, c.result.userId, c.result.postId, c.result.body)
//        .from(Comment as c)
//        .where.eq(c.id, id)
//    }.map(rs => Comment(
//      rs.string(c.resultName.id),
//      rs.string(c.resultName.userId),
//      rs.string(c.resultName.postId),
//      rs.string(c.resultName.body)
//    )).single.apply()
//  }
//
//  private def fetchCommentsByUserId(userId: String)(implicit dbSession: DBSession): Future[List[Comment]] = Future {
//    val c = Comment.syntax("c")
//    withSQL {
//      select(c.result.id, c.result.userId, c.result.postId, c.result.body)
//        .from(Comment as c)
//        .where.eq(c.userId, userId)
//    }.map(rs => Comment(
//      rs.string(c.resultName.id),
//      rs.string(c.resultName.userId),
//      rs.string(c.resultName.postId),
//      rs.string(c.resultName.body)
//    )).list.apply()
//  }
//
//  private def updateUserComment(id: String, userId: String, postId: String, body: String)(implicit dbSession: DBSession): Future[Int] = Future {
//    val c = Comment.column
//    withSQL {
//      update(Comment).set(
//        c.postId -> postId,
//        c.body -> body
//      ).where.eq(c.id, id).and.eq(c.userId, userId) // prevent IDORs on update, todo: have this done implicitly
//    }.update.apply()
//  }
//
//  // dangerous: should only be done by admin
//  private def deleteUserComment(id: String)(implicit dbSession: DBSession): Future[Int] = Future {
//    val c = Comment.column
//    withSQL {
//      delete.from(Comment).where.eq(c.id, id)
//    }.update.apply()
//  }

}
