package model

abstract class ClientState {
  var isWatchingNews = false
  var isAskingQuestion = false
}

case class Authorization() extends ClientState

case class LectureSelect() extends ClientState {
  var lectureNumber = 0
}

object PlaybackStatus extends Enumeration {
  type PlaybackStatus = Value
  var Playing, Paused, Stopped = Value
}

case class SlideSelect() extends ClientState {
  var slideNumber = 0
  var volume = 0.5
  var playbackStatus = PlaybackStatus.Playing
}

object ClientState {
  var state: ClientState = new LectureSelect
}