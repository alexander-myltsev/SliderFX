package guifx

import _root_.controller.Controller
import _root_.model.ClientState
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.stage.Stage
import scalafx.scene.Scene

object View extends JFXApp {
  stage = new Stage {
    width = 1100
    height = 700
    title = "GUI prototype"
    val _scene = new Scene
    scene = _scene

    val controller = new Controller(new ClientState)
    val viewer = new Viewer(controller, _scene)
  }

  //stage.setFullScreen(true)
}