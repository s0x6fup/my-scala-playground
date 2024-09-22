package com.stupidbird.routers

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes.{NoContent, Unauthorized}
import akka.http.scaladsl.model.headers.HttpCookie
import com.stupidbird.services.CommentService._
import com.stupidbird.domains.Comment
import com.stupidbird.utils.UserSession
import com.stupidbird.utils.AuthorizationClient.withAuth
import spray.json._
import scala.util.{Failure, Success}

trait CommentJsonProtocol extends DefaultJsonProtocol {
  implicit val createCommentRequestFormat: RootJsonFormat[CreateCommentRequest] = jsonFormat2(CreateCommentRequest)
  implicit val createCommentResponseFormat: RootJsonFormat[CreateCommentResponse] = jsonFormat1(CreateCommentResponse)
  implicit val CommentFormat: RootJsonFormat[Comment] = jsonFormat4(Comment.apply)
  implicit val GetPostCommentsRequestFormat: RootJsonFormat[GetPostCommentsRequest] = jsonFormat1(GetPostCommentsRequest)
  implicit val GetPostCommentsResponseFormat: RootJsonFormat[GetPostCommentsResponse] = jsonFormat1(GetPostCommentsResponse)
  implicit val getCommentRequestFormat: RootJsonFormat[GetCommentRequest] = jsonFormat1(GetCommentRequest)
  implicit val getCommentResponseFormat: RootJsonFormat[GetCommentResponse] = jsonFormat1(GetCommentResponse)
  implicit val getMyCommentsRequestFormat: RootJsonFormat[GetMyCommentsRequest] = jsonFormat0(GetMyCommentsRequest)
  implicit val getMyCommentsResponseFormat: RootJsonFormat[GetMyCommentsResponse] = jsonFormat1(GetMyCommentsResponse)
  implicit val getUserCommentsRequestFormat: RootJsonFormat[GetUserCommentsRequest] = jsonFormat1(GetUserCommentsRequest)
  implicit val getUserCommentsResponseFormat: RootJsonFormat[GetUserCommentsResponse] = jsonFormat1(GetUserCommentsResponse)
  implicit val updateCommentRequestFormat: RootJsonFormat[UpdateCommentRequest] = jsonFormat2(UpdateCommentRequest)
  implicit val updateCommentResponseFormat: RootJsonFormat[UpdateCommentResponse] = jsonFormat1(UpdateCommentResponse)
  implicit val deleteCommentRequestFormat: RootJsonFormat[DeleteCommentRequest] = jsonFormat1(DeleteCommentRequest)
  implicit val deleteCommentResponseFormat: RootJsonFormat[DeleteCommentResponse] = jsonFormat1(DeleteCommentResponse)
}

object CommentRouter extends CommentJsonProtocol with SprayJsonSupport {
  def apply()(implicit callScope: UserSession): Route = concat(

    path("comment" / "create") {
      post {
        entity(as[CreateCommentRequest])(request => withAuth("comment.create", complete(createComment(request))))
      }
    },

    path("comment" / "post" / Segment) {
      postIdFromPath => get {
        withAuth("comment.read", complete(getPostComments(GetPostCommentsRequest(postIdFromPath))))
      }
    },

    path("comment" / "my") {
      get {
        withAuth("comment.read", complete(getMyComments(GetMyCommentsRequest())))
      }
    },

    path("comment" / "user" / Segment) {
      userIdFromPath: String => get {
        withAuth("comment.read", complete(getUserComments(GetUserCommentsRequest(userIdFromPath))))
      }
    },

    path("comment" / "single" / Segment) {
      CommentIdFromPath: String => get {
        withAuth("comment.read", complete(getSingleComment(GetCommentRequest(CommentIdFromPath))))
      }
    },

    path("comment" / "update" / Segment) {
      CommentIdFromPath: String => post {
        entity(as[UpdateCommentRequest])(request => {
          val overridenRequest = UpdateCommentRequest(
            id = CommentIdFromPath,
            body = request.body
          )
          withAuth("comment.update", complete(updateComment(overridenRequest)))
        })
      }
    },

    // todo: archive Comment (soft delete)
    path("comment" / "delete" / Segment) {
      commentIdFromPath: String => delete {
        withAuth("comment.delete", complete(deleteComment(DeleteCommentRequest(commentIdFromPath))))
      }
    }

  )
}

case class CreateCommentRequest(
                                 postId: String,
                                 body: String
                               )

case class CreateCommentResponse(
                                  id: String
                                )

case class GetPostCommentsRequest(
                                   postId: String
                                 )

case class GetPostCommentsResponse(
                                    comments: Seq[Comment]
                                  )

case class GetCommentRequest(
                              id: String
                            )

case class GetCommentResponse(
                               Comment: Comment
                             )

case class GetMyCommentsRequest(
                               )

case class GetMyCommentsResponse(
                                  Comments: Seq[Comment]
                                )

case class GetUserCommentsRequest(
                                   userId: String
                                 )

case class GetUserCommentsResponse(
                                    Comments: Seq[Comment]
                                  )

case class UpdateCommentRequest(
                                 id: String,
                                 body: String
                               )

case class UpdateCommentResponse(
                                  status: String
                                )

case class DeleteCommentRequest(
                                 id: String
                               )

case class DeleteCommentResponse(
                                  status: String
                                )
