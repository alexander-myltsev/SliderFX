package controller

import view.MainFrame
import model.{Model, ModelImplementation}
import java.io.File

object Launcher {
  def main(args: Array[String]): Unit = {
    val model: Model = new ModelImplementation()
    val controller: Controller = new ControllerImplementation(model)
    MainFrame.launch(controller)

    val tempDir = System.getProperty("java.io.tmpdir")
    val unzippedFilename: String = tempDir + "tmp"
    new File(unzippedFilename).delete
  }
}