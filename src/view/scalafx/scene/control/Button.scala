package scalafx.scene.control

import javafx.scene.{control => jfxsc}

import scalafx.Includes._
import scalafx.util.SFXDelegate

object Button {
  implicit def sfxButton2jfx(v: Button) = v.delegate
}

class Button(override val delegate: jfxsc.Button = new jfxsc.Button()) extends ButtonBase with SFXDelegate[jfxsc.Button] {

}