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
  //  // i need this for nested Comments in get all Comments response
  //  implicit val CommentFormat: RootJsonFormat[Comment] = jsonFormat4(Comment.apply2) // This explicitly tells the compiler to use the apply method of the Comment companion object, which should match the case class constructor
  //  implicit val getAllCommentsRequestFormat: RootJsonFormat[GetAllCommentsRequest] = jsonFormat0(GetAllCommentsRequest)
  //  implicit val getAllCommentsResponseFormat: RootJsonFormat[GetAllCommentsResponse] = jsonFormat1(GetAllCommentsResponse)
  //  implicit val getCommentRequestFormat: RootJsonFormat[GetCommentRequest] = jsonFormat1(GetCommentRequest)
  //  implicit val getCommentResponseFormat: RootJsonFormat[GetCommentResponse] = jsonFormat1(GetCommentResponse)
  //  implicit val getMyCommentsRequestFormat: RootJsonFormat[GetMyCommentsRequest] = jsonFormat0(GetMyCommentsRequest)
  //  implicit val getMyCommentsResponseFormat: RootJsonFormat[GetMyCommentsResponse] = jsonFormat1(GetMyCommentsResponse)
  //  implicit val getUserCommentsRequestFormat: RootJsonFormat[GetUserCommentsRequest] = jsonFormat1(GetUserCommentsRequest)
  //  implicit val getUserCommentsResponseFormat: RootJsonFormat[GetUserCommentsResponse] = jsonFormat1(GetUserCommentsResponse)
  //  implicit val updateCommentRequestFormat: RootJsonFormat[UpdateCommentRequest] = jsonFormat3(UpdateCommentRequest)
  //  implicit val updateCommentResponseFormat: RootJsonFormat[UpdateCommentResponse] = jsonFormat1(UpdateCommentResponse)
  //  implicit val deleteCommentRequestFormat: RootJsonFormat[DeleteCommentRequest] = jsonFormat1(DeleteCommentRequest)
  //  implicit val deleteCommentResponseFormat: RootJsonFormat[DeleteCommentResponse] = jsonFormat1(DeleteCommentResponse)
}

object CommentRouter extends CommentJsonProtocol with SprayJsonSupport {
  def apply()(implicit callScope: UserSession): Route = concat(
    path("comment" / "create") {
      post {
        entity(as[CreateCommentRequest])(request => withAuth("comment.create", complete(createComment(request))))
      }
    },
    //    path("comment" / "all") {
    //      get {
    //        withAuth("Comment.read", complete(getAllComments(GetAllCommentsRequest())))
    //      }
    //    },
    //    path("comment" / "my") {
    //      get {
    //        withAuth("Comment.read", complete(getMyComments(GetMyCommentsRequest())))
    //      }
    //    },
    //    path("comment" / "user" / Segment) { // passing a path variable :)
    //      userIdFromPath: String => get {
    //        withAuth("Comment.read", complete(getUserComments(GetUserCommentsRequest(userIdFromPath))))
    //      }
    //    },
    //    path("comment" / "single" / Segment) {
    //      CommentIdFromPath: String => get {
    //        withAuth("Comment.read", complete(getSingleComment(GetCommentRequest(CommentIdFromPath))))
    //      }
    //    },
    //    path("comment" / "update" / Segment) {
    //      CommentIdFromPath: String => Comment {
    //        entity(as[UpdateCommentRequest])(request => withAuth("Comment.update", complete(updateComment(request))))
    //      }
    //    },
    //    // todo: archive Comment (soft delete)
    //    path("comment" / "delete" / Segment) {
    //      CommentIdFromPath: String => Comment {
    //        entity(as[DeleteCommentRequest])(request => withAuth("Comment.delete", complete(deleteComment(request))))
    //      }
    //    }
  )
}

case class CreateCommentRequest(
                                 postId: String,
                                 body: String
                               )

case class CreateCommentResponse(
                                  id: String
                                )

//case class GetAllCommentsRequest()
//
//case class GetAllCommentsResponse(
//                                Comments: List[Comment]
//                              )
//
//case class GetCommentRequest(
//                           id: String
//                         )
//
//case class GetCommentResponse(
//                            Comment: Comment
//                          )
//
//case class GetMyCommentsRequest(
//                            )
//
//case class GetMyCommentsResponse(
//                               Comment: List[Comment]
//                             )
//
//case class GetUserCommentsRequest(
//                                userId: String
//                              )
//
//case class GetUserCommentsResponse(
//                                 Comment: List[Comment]
//                               )
//
//case class UpdateCommentRequest(
//                              id: String,
//                              title: String,
//                              body: String
//                            )
//
//case class UpdateCommentResponse(
//                               status: String
//                             )
//
//case class DeleteCommentRequest(
//                              id: String
//                            )
//
//case class DeleteCommentResponse(
//                               status: String
//                             )
