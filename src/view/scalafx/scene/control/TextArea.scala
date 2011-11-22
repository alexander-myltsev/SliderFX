package scalafx.scene.control

import javafx.scene.{control => jfxsc}

import scalafx.Includes._
import scalafx.util.SFXDelegate

object TextArea {
  implicit def sfxTextArea2jfx(v: TextArea) = v.delegate
}

class TextArea(override val delegate: jfxsc.TextArea = new jfxsc.TextArea()) extends TextInputControl with SFXDelegate[jfxsc.TextArea] {

}