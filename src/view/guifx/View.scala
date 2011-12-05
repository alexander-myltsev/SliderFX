package guifx

import _root_.controller.Controller
import _root_.model.ClientState
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.stage.Stage
import scalafx.scene.Scene
import javafx.scene.paint.Color._
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import javafx.scene.paint.{Stop, RadialGradient, CycleMethod}

object View extends JFXApp {
  val fog = new Rectangle {
    val fogColor = Color.web("#000000")
    val dark: Color = Color.color(fogColor.getRed, fogColor.getGreen, fogColor.getBlue, 0.7)
    val light: Color = Color.color(fogColor.getRed, fogColor.getGreen, fogColor.getBlue, 0.5)
    val gr: RadialGradient = new RadialGradient(0.0, 0.0, 0.5, 0.5, 1.0, true, CycleMethod.NO_CYCLE, new Stop(0, light), new Stop(1, dark))

    width <== scene.width
    height <== scene.height
    fill = gr
    mouseTransparent = true
  }

  // TODO: remove these nulls
  //var lv: LecturesViewer = null

  //var sv: SlidesViewer = null

  // TODO: simplify stage.scene.value
  /*
  def lectureView: Unit = {
    if (lv == null)
      lv = new LecturesViewer {
        w <== scene.width
        h <== scene.height
      }
    stage.scene.value.content = List(fog, lv)
  }
  */

  // TODO: simplify stage.scene.value
  /*
  def slidesView: Unit = {
    if (sv == null)
      sv = new SlidesViewer {
        w <== scene.width
        h <== scene.height
      }
    stage.scene.value.content = List(sv)
  }
  */

  //def selectLecture(lectureNum: Int): Unit = lv.selectLecture(lectureNum)

  //def selectSlide(slideNum: Int): Unit = sv.selectSlide(slideNum)

  stage = new Stage {
    width = 1350
    height = 800
    title = "GUI prototype"

    scene = new Scene {
      val controller = new Controller(new ClientState) // TODO: fix this dependency
      val viewer = new Viewer(controller) {
        w <== scene.width
        h <== scene.height
      }

      fill = WHITESMOKE
      //fill = Color.color(0, 0, 0, 0.7)
      content = viewer.content
    }
  }

  //stage.setFullScreen(true)

  //lectureView
}