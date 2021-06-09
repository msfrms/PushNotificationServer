# PushNotificationServer

#### for send data via push notification without present in notification center(android)
##### POST /notification/data
##### body: 
```json
{
  "token": "",
  "data": {
    "ping": "hi"
  }
}
```

#### for send data via push notification with present in notification center(android)
##### POST /notification/ui
##### body: 
```json
{
  "token": "",
  "ui": {
    "title": "test",
    "body": "hello"
  }
}
```
