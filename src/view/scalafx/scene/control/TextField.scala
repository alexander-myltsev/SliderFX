package scalafx.scene.control

import javafx.scene.{control => jfxsc}

import scalafx.Includes._
import scalafx.util.SFXDelegate

object TextField {
  implicit def sfxTextField2jfx(v: TextField) = v.delegate
}

class TextField(override val delegate: jfxsc.TextField = new jfxsc.TextField()) extends TextInputControl with SFXDelegate[jfxsc.TextField] {

}