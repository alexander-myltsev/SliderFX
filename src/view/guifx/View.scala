package guifx

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.stage.Stage
import scalafx.scene.Scene
import javafx.scene.paint.Color._

object View extends JFXApp {
  var lv: LecturesViewer = null

  var sv: SlidesViewer = null

  // TODO: simplify stage.scene.value
  def lectureView: Unit = {
    if (lv == null)
      lv = new LecturesViewer {
        w <== scene.width
        h <== scene.height
      }
    stage.scene.value.content = List(lv)
  }

  // TODO: simplify stage.scene.value
  def slidesView: Unit = {
    if (sv == null)
      sv = new SlidesViewer {
        w <== scene.width
        h <== scene.height
      }
    stage.scene.value.content = List(sv)
  }

  def selectLecture(lectureNum: Int): Unit = lv.selectLecture(lectureNum)

  stage = new Stage {
    width = 1350
    height = 800
    title = "GUI prototype"

    scene = new Scene {
      fill = WHITESMOKE
    }
  }

  lectureView
}