package scalafx.scene.control

import javafx.scene.{control => ctrl}
import scalafx.util.SFXDelegate
import scalafx.scene.Node

object Label {
  implicit def sfxHBox2jfx(v: Label) = v.delegate
}

class Label(override val delegate: ctrl.Label = new ctrl.Label()) extends Control with SFXDelegate[ctrl.Label] {
  def text = delegate.textProperty

  def text_=(v: String) {
    //text() = v
    delegate.textProperty.setValue(v)
  }
}