package controller

import view.MainFrame
import model.{Model, ModelImplementation}

object Launcher {
  def main(args: Array[String]) = {
    val model: Model = new ModelImplementation()
    val controller: Controller = new ControllerImplementation(model)
    MainFrame.launch(controller)
  }
}