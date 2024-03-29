package controller

import model._
import javax.imageio.ImageIO
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

abstract class Command

case class GetNewsCmd() extends Command {
  //var news: List[RssChannel] = null
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

case class UpdateContactsCmd(contacts: Contacts) extends Command

case class GetContactsCmd() extends Command {
  var contacts: Contacts = null
}

//case class GetLecturesCountCmd() extends Command {
//  var lecturesCount: Int = -1
//}

//case class GetSlidesCountCmd() extends Command {
//  var slidesCount: Int = -1
//}

case class GetCurrentSlideCmd() extends Command {
  var lectureNumber: Int = -1
  var slideNumber: Int = -1
  var content: SlideInfo = null
}

case class GetLecturesDescriptionsCmd() extends Command {
  var lecturesDescriptions: Array[LectureDescription] = null
}

case class GetSlidesCmd() extends Command {
  var slidesInfo: Array[SlideInfo] = null
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
        cmd.isPassed = (status == Status.Success)

      case (cmd: GetNewsCmd) =>
        val news = model.informationProvider.getNews
        //cmd.news = news.toList
        cmd.newsHtml = model.informationProvider.channelsToHtml(news)

      case (cmd: SelectNextSlideCmd) =>
        model.slideNumber += 1
        val si = model.contentManager.getSlideInfo(model.lectureNumber, model.slideNumber)
        if (si == null) {
          model.slideNumber = 0
          cmd.slideInfo = model.contentManager.getSlideInfo(model.lectureNumber, model.slideNumber)
        } else {
          cmd.slideInfo = si
        }

      case (cmd: SelectLectureCmd) =>
        val lectureNumber = cmd.lectureNumber
        model.lectureNumber = lectureNumber
        cmd.lectureDescription = model.contentManager.getLectureDescription(lectureNumber)
      case (cmd: GetLecturesDescriptionsCmd) => cmd.lecturesDescriptions = model.contentManager.getLecturesDescriptions.toArray

      case (cmd: SelectSlideCmd) =>
        val slideNumber = cmd.slideNumber
        model.slideNumber = slideNumber
        cmd.slideInfo = model.contentManager.getSlideInfo(model.lectureNumber, slideNumber)
      case (cmd: GetSlidesCmd) =>
        cmd.slidesInfo = model.contentManager.getSlidesInfo(model.lectureNumber).toArray

      case (cmd: GetCurrentSlideCmd) =>
        cmd.lectureNumber = model.lectureNumber
        cmd.slideNumber = model.slideNumber
        cmd.content = model.contentManager.getSlideInfo(cmd.lectureNumber, cmd.slideNumber)

      case (cmd: GetCurrentLectureCmd) =>
        cmd.lectureNumber = model.lectureNumber
        cmd.content = model.contentManager.getLectureDescription(cmd.lectureNumber)

      case (cmd: SendQuestionCmd) =>
        val si = model.contentManager.getSlideInfo(model.lectureNumber, model.slideNumber)
        val subject = "Course question from %s (%s). %s".format(model.contacts.fullname, model.contacts.email, model.contacts.organization)
        val os = new ByteArrayOutputStream
        ImageIO.write(si.getContent, "png", os)
        model.informationProvider.sendQuestion(subject, cmd.text, si.getFullTitle, new ByteArrayInputStream(os.toByteArray))

      case (cmd: UpdateContactsCmd) => model.contacts = cmd.contacts

      case (cmd: GetContactsCmd) => cmd.contacts = model.contacts

      //case (cmd: GetLecturesCountCmd) =>
      //  cmd.lecturesCount = ContentManager.getLecturesCount

      //case (cmd: GetSlidesCountCmd) =>
      //  cmd.slidesCount = ContentManager.getSlidesCount(model.lectureNumber)

      case _ => throw new Exception("Unexpected state and command")
    }
  }
}
