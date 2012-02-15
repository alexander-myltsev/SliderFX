package controller

import model._
import model.authorization._
import model.lectures._

abstract class Command

case class GetNewsCmd() extends Command

case class AskQuestionCmd() extends Command

case class AuthorizeCmd(key: String, var isPassed: Boolean) extends Command

case class SelectLectureCmd(lectureNumber: Int) extends Command {
  var lectureDescription: LectureDescription = null
}

case class GetCurrentLectureCmd() extends Command {
  var lectureNumber: Int = -1
}

case class SelectSlideCmd(slideNumber: Int) extends Command

case class GetLecturesDescriptionsCmd() extends Command {
  var lecturesDescriptions: List[LectureDescription] = null
}

case class GetSlidesCmd(lectureNumber: Int) extends Command {
  var slidesInfo: List[SlideInfo] = null
}

trait Controller {
  def executeCommand(command: Command): Unit
}

class ControllerImplementation(model: Model) extends Controller {
  def executeCommand(command: Command): Unit = {
    println("===> executeCommand: " + command)

    (command) match {
      case (cmd: AuthorizeCmd) =>
        val key = new Key(cmd.key)
        val status = Authorizer.authorize(key)
        cmd.isPassed = (status == authorization.Status.Success)

      case (cmd: SelectLectureCmd) =>
        //clientState.lectureNumber = cmd.lectureNumber
        //viewer.setLecture(clientState.lectureNumber)
        println("lecture selected")
      case (cmd: GetLecturesDescriptionsCmd) => cmd.lecturesDescriptions = ContentManager.getLecturesDescriptions

      case (cmd: SelectSlideCmd) =>
        //clientState.slideNumber = cmd.slideNumber
        //viewer.setSlide(clientState.slideNumber)
        println("slide selected")
      case (cmd: GetSlidesCmd) => cmd.slidesInfo = ContentManager.getSlidesInfo(cmd.lectureNumber)

      case (cmd: GetCurrentLectureCmd) =>
        cmd.lectureNumber = model.lectureNumber

      case _ => throw new Exception("Unexpected state and command")
    }
  }
}