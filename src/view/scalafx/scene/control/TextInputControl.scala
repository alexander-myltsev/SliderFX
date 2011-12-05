package scalafx.scene.control

import javafx.scene.{control => jfxsc}

import scalafx.Includes._
import scalafx.util.SFXDelegate

object TextInputControl {
  implicit def sfxTextInputControl2jfx(v: TextInputControl) = v.delegate
}

abstract class TextInputControl extends Control with SFXDelegate[jfxsc.TextInputControl] {
  def text = delegate.textProperty
  def text_=(v: String) {
    text() = v
  }
}