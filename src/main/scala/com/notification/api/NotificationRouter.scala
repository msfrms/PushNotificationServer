package com.notification.api

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Method, Request, Response, Status}
import com.twitter.finagle.http.path.{->, /, Root}
import com.twitter.finagle.http.service.RoutingService
import com.twitter.util.Future

object NotificationRouter {

  def routes: RoutingService[Request] =
    RoutingService.byMethodAndPathObject[Request] { case _ =>
      Service.mk(_ => Future.value(Response(Status.NotFound)))
    }
}
