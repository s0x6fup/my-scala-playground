package com.stupidbird.services

import com.stupidbird.StupidbirdService.{dbSession, executionContext}
import com.stupidbird.utils.SessionService._
import com.stupidbird.domains._
import com.stupidbird.routers._
import scalikejdbc._
import scala.concurrent.Future
import java.util.UUID.randomUUID
import com.stupidbird.utils.UserSession

object PostService {

  def createPost(request: CreatePostRequest)(implicit callScope: UserSession): Future[CreatePostResponse] = {
    for {
      maybePostId <- createNewPost(callScope.userId, request.title, request.body)
    } yield CreatePostResponse(maybePostId)
  }

  def getAllPosts(request: GetAllPostsRequest)(implicit callScope: UserSession): Future[GetAllPostsResponse] = {
    for {
      allPosts <- fetchAllPosts()
    } yield GetAllPostsResponse(allPosts)
  }

  def getSinglePost(request: GetPostRequest)(implicit callScope: UserSession): Future[GetPostResponse] = {
    for {
      post <- fetchPostById(request.id)
    } yield GetPostResponse(post.getOrElse(Post.empty()))
  }

  def getMyPosts(request: GetMyPostsRequest)(implicit callScope: UserSession): Future[GetMyPostsResponse] = {
    for {
      myPosts <- fetchPostsByUserId(callScope.userId)
    } yield GetMyPostsResponse(myPosts)
  }

  def getUserPosts(request: GetUserPostsRequest)(implicit callScope: UserSession): Future[GetUserPostsResponse] = {
    for {
      userPosts <- fetchPostsByUserId(callScope.userId)
    } yield GetUserPostsResponse(userPosts)
  }

  def updatePost(request: UpdatePostRequest)(implicit callScope: UserSession): Future[UpdatePostResponse] = {
    for {
      status <- updateUserPost(request.id, callScope.userId, request.title, request.body)
    } yield UpdatePostResponse(
        if (status == 0) "Failed to update"
        else if (status == 1) "Successfully updated"
        else "Unexpected behavior"
    )
  }

  def deletePost(request: DeletePostRequest)(implicit callScope: UserSession): Future[DeletePostResponse] = {
    for {
      status <- deleteUserPost(request.id)
    } yield DeletePostResponse(
        if (status == 0) "Failed to update"
        else if (status == 1) "Successfully updated"
        else "Unexpected behavior"
    )
  }

  private def createNewPost(userId: String, title: String, body: String)(implicit dbSession: DBSession): Future[String] = Future {
    val generatedId = randomUUID.toString
    val p = Post.column
    withSQL {
      insert.into(Post).namedValues(
        p.id -> generatedId,
        p.userId -> userId,
        p.title -> title,
        p.body -> body
      )
    }.update.apply()
    generatedId
  }

  private def fetchAllPosts()(implicit dbSession: DBSession): Future[List[Post]] = Future {
    val p = Post.syntax("p")
    withSQL {
      select(p.result.id, p.result.userId, p.result.title, p.result.body)
        .from(Post as p) // i can have IF statement here for optional Id to fetch either one or many posts (prone to issues?)
    }.map(rs => Post(
      rs.string(p.resultName.id),
      rs.string(p.resultName.userId),
      rs.string(p.resultName.title),
      rs.string(p.resultName.body)
    )).list.apply()
  }

  private def fetchPostById(id: String)(implicit dbSession: DBSession): Future[Option[Post]] = Future {
    val p = Post.syntax("p")
    withSQL {
      select(p.result.id, p.result.userId, p.result.title, p.result.body)
        .from(Post as p)
        .where.eq(p.id, id)
    }.map(rs => Post(
      rs.string(p.resultName.id),
      rs.string(p.resultName.userId),
      rs.string(p.resultName.title),
      rs.string(p.resultName.body)
    )).single.apply()
  }

  private def fetchPostsByUserId(userId: String)(implicit dbSession: DBSession): Future[List[Post]] = Future {
    val p = Post.syntax("p")
    withSQL {
      select(p.result.id, p.result.userId, p.result.title, p.result.body)
        .from(Post as p)
        .where.eq(p.userId, userId)
    }.map(rs => Post(
      rs.string(p.resultName.id),
      rs.string(p.resultName.userId),
      rs.string(p.resultName.title),
      rs.string(p.resultName.body)
    )).list.apply()
  }

  private def updateUserPost(id: String, userId: String, title: String, body: String)(implicit dbSession: DBSession): Future[Int] = Future {
    val p = Post.column
    withSQL {
      update(Post).set(
        p.title -> title,
        p.body -> body
      ).where.eq(p.id, id).and.eq(p.userId, userId) // prevent IDORs on update, todo: have this done implicitly
    }.update.apply()
  }

  // dangerous: should only be done by admin
  private def deleteUserPost(id: String)(implicit dbSession: DBSession): Future[Int] = Future {
    val p = Post.column
    withSQL {
      delete.from(Post).where.eq(p.id, id)
    }.update.apply()
  }

}
