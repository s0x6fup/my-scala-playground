package com.stupidbird.models

case class User(
                 id: String,
                 email: String,
                 hash: String
               )

case class Workspace(
                      id: String,
                      name: String,
                      imageUrl: String,
                      description: String
                    )

case class Note(
                 id: String,
                 title: String,
                 text: String
               )

//object User extends SQLSyntaxSupport[User] {
//  override val tableName = "users"
//  override val columnNames = Seq("id", "email", "hash", "created_timestamp", "updated_timestamp")
//}
