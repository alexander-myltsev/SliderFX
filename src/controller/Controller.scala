package controller

import _root_.model._
import model.authorization._
import model.lectures._

abstract class Command

case class WatchNewsCmd(turnOn: Boolean) extends Command

case class AskQuestionCmd(turnOn: Boolean) extends Command

case class Authorize(key: String) extends Command

case class SelectLecture(lectureNumber: Int) extends Command

case class SlideCmd(slideNumber: Int) extends Command

case class VolumeCmd(volume: Double) extends Command

case class PlaybackStatusCmd(playbackStatus: PlaybackStatus.Value) extends Command

case class GoBack() extends Command

case class GetLecturesDescriptions(var lecturesDescriptions: List[LectureDescription] = null) extends Command

object Controller {
  def executeCommand(command: Command) = (ClientState.state, command) match {
    case ((cs: ClientState), (cmd: WatchNewsCmd)) => cs.isWatchingNews = cmd.turnOn
    case ((cs: ClientState), (cmd: AskQuestionCmd)) => cs.isAskingQuestion = cmd.turnOn
    case ((cs: ClientState), (cmd: Authorize)) =>
      val key = new Key(cmd.key)
      if (Authorizer.authorize(key) == authorization.Status.Success) ClientState.state = new LectureSelect

    case ((ls: LectureSelect), (cmd: SelectLecture)) => ls.lectureNumber = cmd.lectureNumber
    case ((ls: LectureSelect), (cmd: SlideCmd)) => ClientState.state = new SlideSelect()
    case ((ls: LectureSelect), (cmd: GetLecturesDescriptions)) => cmd.lecturesDescriptions = LectureManager.getLecturesDescriptions()
    case ((ls: LectureSelect), (cmd: GoBack)) => ClientState.state = new Authorization

    case ((ss: SlideSelect), (cmd: SlideCmd)) => ss.slideNumber = cmd.slideNumber
    case ((ss: SlideSelect), (cmd: VolumeCmd)) => ss.volume = cmd.volume
    case ((ss: SlideSelect), (cmd: PlaybackStatusCmd)) => ss.playbackStatus = cmd.playbackStatus
    case ((ss: SlideSelect), (cmd: GoBack)) => ClientState.state = new LectureSelect

    case _ => throw new Exception("Unexpected state and command")
  }
}