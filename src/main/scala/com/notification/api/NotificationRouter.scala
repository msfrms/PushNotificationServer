package com.notification.api

import com.notification.push.{MessageService, Push}

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Method, Request, Response, Status}
import com.twitter.finagle.http.path.{->, /, Root}
import com.twitter.finagle.http.service.RoutingService
import com.twitter.util.Future

import io.circe.generic.auto._
import io.circe.parser.decode

final case class TokenData(token: String, data: Map[String, String])

final case class Ui(title: String, body: String)

final case class TokenUi(token: String, ui: Ui)

object NotificationRouter {

  def routes: RoutingService[Request] =
    RoutingService.byMethodAndPathObject[Request] {

      case Method.Post -> Root / "notification" / "data" =>
        Service.mk { request =>
          val body = decode[TokenData](request.contentString)
          body match {
            case Left(_) => Future.value(Response(Status.BadRequest))
            case Right(value) =>
              MessageService(value.token)
                .apply(Push.Message(value.data))
                .map(_ => Response(Status.Ok))
          }
        }

      case Method.Post -> Root / "notification" / "ui" =>
        Service.mk { request =>
          val body = decode[TokenUi](request.contentString)
          body match {
            case Left(_) => Future.value(Response(Status.BadRequest))
            case Right(value) =>
              MessageService(value.token)
                .apply(
                  Push
                    .Notification(title = value.ui.title, body = value.ui.body)
                )
                .map(_ => Response(Status.Ok))
          }
        }

      case _ => Service.mk(_ => Future.value(Response(Status.NotFound)))
    }
}
