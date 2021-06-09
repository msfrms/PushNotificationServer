package com.notification.push

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.{FirebaseApp, FirebaseOptions}
import com.google.firebase.messaging.{
  FirebaseMessaging,
  FirebaseMessagingException,
  Message => FirebaseMessage,
  Notification => FirebaseNotification
}

import com.twitter.finagle.Service
import com.twitter.util.{Future, FuturePool}

import com.typesafe.config.ConfigFactory

import scala.jdk.CollectionConverters._

sealed trait Push

object Push {
  final case class Message(data: Map[String, String])        extends Push
  final case class Notification(title: String, body: String) extends Push
}

final class MessageService(token: String) extends Service[Push, Unit] {

  private val googleCredentials: GoogleCredentials = {
    val config = ConfigFactory.load()
    val token = getClass.getResourceAsStream(
      s"/${config.getString("push.config")}"
    )
    GoogleCredentials.fromStream(token)
  }

  private val firebaseMessaging: FirebaseMessaging = {

    val options = new FirebaseOptions.Builder()
      .setCredentials(googleCredentials)
      .build()

    FirebaseMessaging.getInstance(FirebaseApp.initializeApp(options))
  }

  override def apply(push: Push): Future[Unit] =
    FuturePool.unboundedPool {
      val message: FirebaseMessage = push match {

        case message: Push.Message =>
          FirebaseMessage
            .builder()
            .setToken(token)
            .putAllData(message.data.map { case (key, value) =>
              (key, value)
            }.asJava)
            .build()

        case notification: Push.Notification =>
          FirebaseMessage
            .builder()
            .setToken(token)
            .setNotification(
              new FirebaseNotification(
                notification.title,
                notification.body
              )
            )
            .build()
      }

      firebaseMessaging.send(message)
    }
      .flatMap(_ => Future.Unit)
      .rescue {

        case e: FirebaseMessagingException =>
          Future.exception(
            FirebaseMessagingExceptionWrapper(
              token = token,
              errorCode = e.getErrorCode,
              message = e.getMessage,
              cause = e.getCause
            )
          )

        case exception => Future.exception(exception)
      }
}

object MessageService {

  def apply(token: String): Service[Push, Unit] =
    new MessageService(token)
}
