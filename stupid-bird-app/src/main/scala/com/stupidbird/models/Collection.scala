package com.stupidbird.models

case class Collection(
                       id: String,
                       title: String,
                       description: String,
                       requests: Seq[CollectionRequest]
                     )

case class CollectionRequest(
                              id: String,
                              title: String,
                              description: String,
                              method: RequestMethod,
                              url: String,
                              headers: Seq[RequestHeader]
                            )

case class RequestMethod(
                          value: String
                        )

object RequestMethod {
  def apply(value: String): RequestMethod = {
    require(Seq("GET", "POST", "PUT", "DELETE").contains(value), s"invalid protocol: $value")
    new RequestMethod(value)
  }
}

case class RequestHeader(header: String, value: String)