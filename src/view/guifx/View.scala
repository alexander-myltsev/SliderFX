package guifx

import _root_.controller.Controller
import _root_.model.ClientState
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.stage.Stage
import scalafx.scene.Scene
import scalafx.stage.Stage._

object View extends JFXApp {
  val ratio = 1.46

  stage = new Stage {
    height = 700
    width = height * ratio
    title = "GUI prototype"
    val _scene = new Scene
    scene = _scene

    val controller = new Controller(new ClientState)
    val viewer = new Viewer(controller, _scene)
  }

  /*
  stage.heightProperty onChange {
    (prop, oldVal, newVal) => {
      if (oldVal != newVal) {
        //println("height: " + newVal)
        stage.setWidth(newVal.doubleValue * ratio)
        //println("heightProperty onChange: " + stage.getWidth + " | " + stage.getHeight)
      }
    }
  }

  stage.widthProperty onChange {
    (prop, oldVal, newVal) => {
      if (oldVal != newVal) {
        //println("width: " + newVal)
        stage.setHeight(newVal.doubleValue / ratio)
        //println("widthProperty onChange: " + stage.getWidth + " | " + stage.getHeight)
      }
    }
  }
  */

  //stage.setFullScreen(true)
}