package guifx

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.stage.Stage
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import javafx.scene.paint.Color._
import scalafx.scene.shape.Rectangle
import javafx.beans.property.SimpleDoubleProperty


object Main1 extends JFXApp {
  stage = new Stage {
    width = 1200
    height = 800
    title = "GUI prototype"

    scene = new Scene {
      fill = BLACK

      content = List(new guifx.LectureSelector {
        w <== scene.width
        h <== scene.height
      })
    }
  }
}