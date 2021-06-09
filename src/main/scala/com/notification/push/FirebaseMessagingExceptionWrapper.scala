package com.notification.push

final case class FirebaseMessagingExceptionWrapper(
    token: String,
    errorCode: String,
    message: String,
    cause: Throwable
) extends Throwable(cause) {

  override def toString: String = super.toString +
    s"""
       |token: $token
       |errorCode: $errorCode
       |message: $message
       |""".stripMargin
}
