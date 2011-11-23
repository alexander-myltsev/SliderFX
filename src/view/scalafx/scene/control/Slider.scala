package scalafx.scene.control

import javafx.scene.{control => jfxsc}

import scalafx.Includes._
import scalafx.util.SFXDelegate

object Slider {
  implicit def sfxSlider2jfx(v: Slider) = v.delegate
}

class Slider(override val delegate: jfxsc.Slider = new jfxsc.Slider()) extends Control with SFXDelegate[jfxsc.Slider] {

}