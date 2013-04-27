package controller

import view.MainFrame
import java.io.File
import model._

object Launcher {
  def main(args: Array[String]): Unit = {
    val model: Model = new ModelImplementation(new InformationProvider, new ContentManager(new AesEncrypter))
    val controller: Controller = new ControllerImplementation(model)
    MainFrame.launch(controller)

    val tempDir = System.getProperty("java.io.tmpdir")
    val unzippedFilename: String = tempDir + "tmp"
    new File(unzippedFilename).delete
  }
}