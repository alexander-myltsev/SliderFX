package scalafx.scene.image

import scalafx.Includes._
import scalafx.scene._
import scalafx.util.SFXDelegate
import javafx.scene.{image => img}

object ImageView {
  implicit def sfxImageView2jfx(v: img.ImageView) = v.delegate
}

class ImageView(override val delegate: img.ImageView = new img.ImageView()) extends Node with SFXDelegate[img.ImageView] {
  def image = delegate.imageProperty
  def image_=(url: String) {
    image() = new img.Image(url)
  }
  def image_=(param: (String, Double, Double)) {
    val (url, requestedWidth, requestedHeight) = param
    image() = new img.Image(url, requestedWidth, requestedHeight, true, true, true)
  }

  def fitWidth = delegate.fitWidthProperty
  def fitWidth_=(w: Double) {
    fitWidth() = w
  }

  def fitHeight = delegate.fitHeightProperty
  def fitHeight_=(h: Double) {
    fitHeight() = h
  }

  def preserveRatio = delegate.preserveRatioProperty
  def preserveRatio_=(v: Boolean) = {
    preserveRatio() = v
  }
}