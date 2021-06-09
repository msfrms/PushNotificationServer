package com.notification.api

import com.twitter.util.Await

object HttpServer {

  def main(args: Array[String]): Unit = Await.ready(Endpoint.forNotification)
}
