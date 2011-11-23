package controller

import _root_.model._
import model.authorization._
import model.lectures._
import guifx.View

abstract class Command

case class WatchNewsCmd(turnOn: Boolean) extends Command

case class AskQuestionCmd(turnOn: Boolean) extends Command

case class AuthorizeCmd(key: String) extends Command

case class SelectLectureCmd(lectureNumber: Int) extends Command

case class SelectSlideCmd(slideNumber: Int) extends Command

case class VolumeCmd(volume: Double) extends Command

case class PlaybackStatusCmd(playbackStatus: PlaybackStatus.Value) extends Command

case class GoForwardCmd() extends Command

case class GoBackCmd() extends Command

case class GetLecturesDescriptionsCmd(var lecturesDescriptions: List[LectureDescription] = null) extends Command

case class GetSlidesCmd(lectureNumber: Int, var slidesInfo: List[SlideInfo] = null) extends Command

object Controller {
  def executeCommand(command: Command): Unit = {
    println("===> executeCommand: " + ClientState.state + " | " + command)

    (ClientState.state, command) match {
      case ((cs: ClientState), (cmd: WatchNewsCmd)) => cs.isWatchingNews = cmd.turnOn
      case ((cs: ClientState), (cmd: AskQuestionCmd)) => cs.isAskingQuestion = cmd.turnOn
      case ((cs: ClientState), (cmd: AuthorizeCmd)) =>
        val key = new Key(cmd.key)
        if (Authorizer.authorize(key) == authorization.Status.Success) ClientState.state = new LectureSelect

      case ((ls: LectureSelect), (cmd: SelectLectureCmd)) =>
        ls.lectureNumber = cmd.lectureNumber
        View.selectLecture(ls.lectureNumber)
      case ((ls: LectureSelect), (cmd: GoForwardCmd)) =>
        ClientState.state = new SlideSelect
        View.slidesView
      case ((ls: LectureSelect), (cmd: GetLecturesDescriptionsCmd)) => cmd.lecturesDescriptions = LectureManager.getLecturesDescriptions
      case ((ls: LectureSelect), (cmd: GoBackCmd)) => ClientState.state = new Authorization

      case ((ss: SlideSelect), (cmd: SelectSlideCmd)) => ss.slideNumber = cmd.slideNumber
      case ((ss: SlideSelect), (cmd: VolumeCmd)) => ss.volume = cmd.volume
      case ((ss: SlideSelect), (cmd: PlaybackStatusCmd)) => ss.playbackStatus = cmd.playbackStatus
      case ((ls: SlideSelect), (cmd: GetSlidesCmd)) => cmd.slidesInfo = LectureManager.getSlidesInfo(cmd.lectureNumber)
      case ((ss: SlideSelect), (cmd: GoBackCmd)) =>
        ClientState.state = new LectureSelect
        View.lectureView

      case _ => throw new Exception("Unexpected state and command")
    }
  }
}