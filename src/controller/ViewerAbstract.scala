package controller

object ViewerState extends Enumeration {
  type t = Value
  var Authorizing, SelectingLecture, SelectingSlides = Value
}

abstract class ViewerAbstract {
  private var _state = ViewerState.SelectingLecture
  def state = _state
  def state_=(v: ViewerState.t) = {
    _state = v
  }

  def setLecture(lectureNum: Int)

  def setSlide(slideNum: Int)

  def updateNews(news: List[String])
}