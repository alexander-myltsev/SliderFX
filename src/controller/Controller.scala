package controller

import _root_.model._
import model.authorization._
import model.lectures._

abstract class Command

case class WatchNewsCmd() extends Command

case class AskQuestionCmd(turnOn: Boolean) extends Command

case class AuthorizeCmd(key: String, var isPassed: Boolean) extends Command

case class SelectLectureCmd(lectureNumber: Int) extends Command

case class SelectSlideCmd(slideNumber: Int) extends Command

case class GetLecturesDescriptionsCmd(var lecturesDescriptions: List[LectureDescription] = null) extends Command

case class GetSlidesCmd(lectureNumber: Int, var slidesInfo: List[SlideInfo] = null) extends Command

case class GoBackCmd() extends Command

case class GoForwardCmd() extends Command

class Controller(clientState: ClientState) {
  def executeCommand(viewer: ViewerAbstract, command: Command): Unit = {
    println("===> executeCommand: " + viewer.state + " | " + command)

    (command) match {
      case (cmd: WatchNewsCmd) =>
        val news = InformationProvider.getNews
        viewer.updateNews(news map (_.content))

      case (cmd: AuthorizeCmd) =>
        val key = new Key(cmd.key)
        val status = Authorizer.authorize(key)
        cmd.isPassed = (status == authorization.Status.Success)

      case (cmd: SelectLectureCmd) =>
        clientState.lectureNumber = cmd.lectureNumber
        viewer.setLecture(clientState.lectureNumber)
      case (cmd: GetLecturesDescriptionsCmd) => cmd.lecturesDescriptions = ContentManager.getLecturesDescriptions

      case (cmd: SelectSlideCmd) =>
        clientState.slideNumber = cmd.slideNumber
        viewer.setSlide(clientState.slideNumber)
      case (cmd: GetSlidesCmd) => cmd.slidesInfo = ContentManager.getSlidesInfo(cmd.lectureNumber)

      case (cmd: GoBackCmd) if viewer.state == ViewerState.Authorizing =>
        throw new Exception("Can't go back while ViewerState.Authorizing")
      case (cmd: GoBackCmd) if viewer.state == ViewerState.SelectingLecture =>
        //viewer.state = ViewerState.SelectingSlides
        viewer.setSlide(clientState.slideNumber)
      case (cmd: GoBackCmd) if viewer.state == ViewerState.SelectingSlides =>
        //viewer.state = ViewerState.SelectingLecture
        viewer.setLecture(clientState.lectureNumber)


      case (cmd: GoForwardCmd) if viewer.state == ViewerState.Authorizing =>
        //viewer.state = ViewerState.SelectingLecture
        viewer.setLecture(clientState.lectureNumber)
      case (cmd: GoForwardCmd) if viewer.state == ViewerState.SelectingLecture =>
        //viewer.state = ViewerState.SelectingSlides
        viewer.setSlide(clientState.slideNumber)
      case (cmd: GoForwardCmd) if viewer.state == ViewerState.SelectingSlides =>
        throw new Exception("Can't go forward while ViewerState.SelectingSlides")

      case _ => throw new Exception("Unexpected state and command")
    }
  }
}