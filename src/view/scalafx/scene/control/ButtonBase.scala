package scalafx.scene.control

import javafx.scene.{control => jfxsc}

import scalafx.Includes._
import scalafx.util.SFXDelegate

object ButtonBase {
  implicit def sfxButtonBase2jfx(v: ButtonBase) = v.delegate
}

abstract class ButtonBase extends Labeled with SFXDelegate[jfxsc.ButtonBase] {

}