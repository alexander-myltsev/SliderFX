package guifx

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.stage.Stage
import scalafx.scene.Scene
import javafx.scene.paint.Color._

object View extends JFXApp {
  def lectureView: Unit = {
    stage.scene.value.content = List(new ContentViewer {
      w <== scene.width
      h <== scene.height
    }.lectureParts)
  }

  def slidesView: Unit = {
    println("slidesView")
    stage.scene.value.content = List(new ContentViewer {
      w <== scene.width
      h <== scene.height
    }.slidesParts)
  }

  stage = new Stage {
    width = 1200
    height = 800
    title = "GUI prototype"

    scene = new Scene {
      fill = WHITE
    }
  }

  lectureView
}