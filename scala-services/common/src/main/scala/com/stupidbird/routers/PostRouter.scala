package com.stupidbird.routers

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes.{NoContent, Unauthorized}
import akka.http.scaladsl.model.headers.HttpCookie
import com.stupidbird.services.PostService._
import com.stupidbird.domains.Post
import com.stupidbird.utils.UserSession
import com.stupidbird.utils.AuthorizationClient.withAuth
import spray.json._
import scala.util.{Failure, Success}

trait PostJsonProtocol extends DefaultJsonProtocol {
  implicit val createPostRequestFormat: RootJsonFormat[CreatePostRequest] = jsonFormat2(CreatePostRequest)
  implicit val createPostResponseFormat: RootJsonFormat[CreatePostResponse] = jsonFormat1(CreatePostResponse)
  // i need this for nested posts in get all posts response
  implicit val postFormat: RootJsonFormat[Post] = jsonFormat4(Post.apply2) // This explicitly tells the compiler to use the apply method of the Post companion object, which should match the case class constructor
  implicit val getAllPostsRequestFormat: RootJsonFormat[GetAllPostsRequest] = jsonFormat0(GetAllPostsRequest)
  implicit val getAllPostsResponseFormat: RootJsonFormat[GetAllPostsResponse] = jsonFormat1(GetAllPostsResponse)
  implicit val getPostRequestFormat: RootJsonFormat[GetPostRequest] = jsonFormat1(GetPostRequest)
  implicit val getPostResponseFormat: RootJsonFormat[GetPostResponse] = jsonFormat1(GetPostResponse)
  implicit val getMyPostsRequestFormat: RootJsonFormat[GetMyPostsRequest] = jsonFormat0(GetMyPostsRequest)
  implicit val getMyPostsResponseFormat: RootJsonFormat[GetMyPostsResponse] = jsonFormat1(GetMyPostsResponse)
  implicit val getUserPostsRequestFormat: RootJsonFormat[GetUserPostsRequest] = jsonFormat1(GetUserPostsRequest)
  implicit val getUserPostsResponseFormat: RootJsonFormat[GetUserPostsResponse] = jsonFormat1(GetUserPostsResponse)
  implicit val updatePostRequestFormat: RootJsonFormat[UpdatePostRequest] = jsonFormat3(UpdatePostRequest)
  implicit val updatePostResponseFormat: RootJsonFormat[UpdatePostResponse] = jsonFormat1(UpdatePostResponse)
  implicit val deletePostRequestFormat: RootJsonFormat[DeletePostRequest] = jsonFormat1(DeletePostRequest)
  implicit val deletePostResponseFormat: RootJsonFormat[DeletePostResponse] = jsonFormat1(DeletePostResponse)
}

object PostRouter extends PostJsonProtocol with SprayJsonSupport {
  def apply()(implicit callScope: UserSession): Route = concat(
    path("post" / "create") {
      post {
        entity(as[CreatePostRequest])(request => withAuth("post.create", complete(createPost(request))))
      }
    },
    path("post" / "all") {
      get {
        withAuth("post.read", complete(getAllPosts(GetAllPostsRequest())))
      }
    },
    path("post" / "my") {
      get {
        withAuth("post.read", complete(getMyPosts(GetMyPostsRequest())))
      }
    },
    path("post" / "user" / Segment) { // passing a path variable :)
      userIdFromPath: String => get {
        withAuth("post.read", complete(getUserPosts(GetUserPostsRequest(userIdFromPath))))
      }
    },
    path("post" / "single" / Segment) {
      postIdFromPath: String => get {
        withAuth("post.read", complete(getSinglePost(GetPostRequest(postIdFromPath))))
      }
    },
    path("post" / "update" / Segment) {
      postIdFromPath: String => post {
        entity(as[UpdatePostRequest])(request => withAuth("post.update", complete(updatePost(request))))
      }
    },
    // todo: archive post (soft delete)
    path("post" / "delete" / Segment) {
      postIdFromPath: String => delete {
        withAuth("post.delete", complete(deletePost(DeletePostRequest(postIdFromPath))))
      }
    }
  )
}

case class CreatePostRequest(
                              title: String,
                              body: String
                            )

case class CreatePostResponse(
                               id: String
                             )

case class GetAllPostsRequest()

case class GetAllPostsResponse(
                                posts: List[Post]
                              )

case class GetPostRequest(
                           id: String
                         )

case class GetPostResponse(
                            post: Post
                          )

case class GetMyPostsRequest(
                            )

case class GetMyPostsResponse(
                               post: List[Post]
                             )

case class GetUserPostsRequest(
                                userId: String
                              )

case class GetUserPostsResponse(
                                 post: List[Post]
                               )

case class UpdatePostRequest(
                              id: String,
                              title: String,
                              body: String
                            )

case class UpdatePostResponse(
                               status: String
                             )

case class DeletePostRequest(
                              id: String
                            )

case class DeletePostResponse(
                               status: String
                             )