package com.stupidbird.utils

import com.stupidbird.utils.RolesConfig._

object PermissionsConfig {
  def apply(): Map[String, Seq[String]] = Map(
    "health.read" -> Seq(Admin, User, Anonymous),

    // authentication permissions
    "authn.register" -> Seq(Admin, User, Anonymous),
    "authn.login" -> Seq(Admin, User, Anonymous),
    "authn.logout" -> Seq(Admin, User),
    "authn.logoutAll" -> Seq(Admin, User),
    "authn.listAllSessions" -> Seq(Admin, User),

    // posts permissions
    "post.create" -> Seq(Admin, User),
    "post.read" -> Seq(Admin, User, Anonymous),
    "post.update" -> Seq(Admin, User),
    "post.softDelete" -> Seq(Admin, User),
    "post.delete" -> Seq(Admin),

    // comment permissions
    "comment.create" -> Seq(Admin, User),
    "comment.read" -> Seq(Admin, User, Anonymous),
    "comment.update" -> Seq(Admin, User, Anonymous),
    "comment.softDelete" -> Seq(Admin, User, Anonymous),
    "comment.delete" -> Seq(Admin, Anonymous),

  )
}

