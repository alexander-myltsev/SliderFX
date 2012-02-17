package controller

import model._
import model.authorization._
import model.lectures._
import java.awt.image.BufferedImage

abstract class Command

case class GetNewsCmd() extends Command {
  var news: List[RssChannel] = null
  var newsHtml: String = null
}

case class AskQuestionCmd() extends Command

case class AuthorizeCmd(key: String, var isPassed: Boolean) extends Command

case class SelectLectureCmd(lectureNumber: Int) extends Command {
  var lectureDescription: LectureDescription = null
}

case class SelectSlideCmd(slideNumber: Int) extends Command {
  var slideInfo: SlideInfo = null
}

case class SelectNextSlideCmd() extends Command {
  var slideInfo: SlideInfo = null
}

case class GetCurrentLectureCmd() extends Command {
  var lectureNumber: Int = -1
  var content: LectureDescription = null
}

case class GetCurrentSlideCmd() extends Command {
  var lectureNumber: Int = -1
  var slideNumber: Int = -1
  var content: SlideInfo = null
}

case class GetLecturesDescriptionsCmd() extends Command {
  var lecturesDescriptions: List[LectureDescription] = null
}

case class GetSlidesCmd(lectureNumber: Int) extends Command {
  var slidesInfo: List[SlideInfo] = null
}

case class SendQuestionCmd(text: String) extends Command {

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

      case (cmd: GetNewsCmd) =>
        val news = InformationProvider.getNews()
        cmd.news = news.toList
        cmd.newsHtml = InformationProvider.channelsToHtml(news)

      case (cmd: SelectNextSlideCmd) =>
        model.slideNumber += 1
        val si = ContentManager.getSlideInfo(model.lectureNumber, model.slideNumber)
        cmd.slideInfo = si

      case (cmd: SelectLectureCmd) =>
        val lectureNumber = cmd.lectureNumber
        model.lectureNumber = lectureNumber
        cmd.lectureDescription = ContentManager.getLectureDescription(lectureNumber)
      case (cmd: GetLecturesDescriptionsCmd) => cmd.lecturesDescriptions = ContentManager.getLecturesDescriptions

      case (cmd: SelectSlideCmd) =>
        val slideNumber = cmd.slideNumber
        model.slideNumber = slideNumber
        cmd.slideInfo = ContentManager.getSlideInfo(model.lectureNumber, slideNumber)
      case (cmd: GetSlidesCmd) => cmd.slidesInfo = ContentManager.getSlidesInfo(cmd.lectureNumber)

      case (cmd: GetCurrentSlideCmd) =>
        cmd.lectureNumber = model.lectureNumber
        cmd.slideNumber = model.slideNumber
        cmd.content = ContentManager.getSlideInfo(cmd.lectureNumber, cmd.slideNumber)

      case (cmd: GetCurrentLectureCmd) =>
        cmd.lectureNumber = model.lectureNumber
        cmd.content = ContentManager.getLectureDescription(cmd.lectureNumber)

      case (cmd: SendQuestionCmd) =>
        val si = ContentManager.getSlideInfo(model.lectureNumber, model.slideNumber)
        InformationProvider.sendQuestion("CourseGUI question", cmd.text, si.path)

      case _ => throw new Exception("Unexpected state and command")
    }
  }
}