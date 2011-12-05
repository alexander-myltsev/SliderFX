package controller

object ViewerState extends Enumeration {
  type ViewerState = Value
  var Authorizing, SelectingLecture, SelectingSlides = Value
}

abstract class ViewerAbstract {
  var state = ViewerState.SelectingLecture

  def setLecture(lectureNum: Int)

  def setSlide(slideNum: Int)

  def updateNews(news: List[String])
}