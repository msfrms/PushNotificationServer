package com.notification.api

import com.twitter.finagle.{Http, ListeningServer}

import com.typesafe.config.ConfigFactory

object Endpoint {
  def forNotification: ListeningServer = {
    val config = ConfigFactory.load()
    Http.server
      .serve(s":${config.getInt("api.port")}", NotificationRouter.routes)
  }
}
