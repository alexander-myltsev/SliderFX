package controller

import view.MainFrame
import model._

object Launcher {
  def main(args: Array[String]): Unit = {
    val cm: ContentManager = new ContentManager(new AesEncrypter)
    val model: Model = new ModelImplementation(new InformationProvider, cm)
    val controller: Controller = new ControllerImplementation(model)
    MainFrame.launch(controller)
  }
}