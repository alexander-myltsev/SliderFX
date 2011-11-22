package scalafx.scene.control

import javafx.scene.{control => jfxsc}

import scalafx.Includes._
import scalafx.scene.Node
import scalafx.util.SFXDelegate

object Labeled {
  implicit def sfxLabeled2jfx(v: Labeled) = v.delegate
}

abstract class Labeled extends Control with SFXDelegate[jfxsc.Labeled] {
  def text = delegate.textProperty
  def text_=(s:String) {
    text() = s
  }
}