package model

class ClientState() {
  private var _slideNumber = 1

  def slideNumber = _slideNumber

  def slideNumber_=(v: Int) = _slideNumber = v

  // ToDo: Do it for other properties.

  var lectureNumber = 1
}