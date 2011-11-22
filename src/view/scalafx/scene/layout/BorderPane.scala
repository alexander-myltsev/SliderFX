package scalafx.scene.layout

import javafx.scene.{layout => jfxsl}
import scalafx.Includes._
import scalafx.util.SFXDelegate
import javafx.geometry.Pos
import scalafx.scene.Node

object BorderPane {
  implicit def sfxBorderPane2jfx(v: BorderPane) = v.delegate
}

class BorderPane(override val delegate: jfxsl.BorderPane = new jfxsl.BorderPane()) extends Pane with SFXDelegate[jfxsl.BorderPane] {
  def top = delegate.topProperty

  def top_=(n: Node) {
    top() = n
  }

  def right = delegate.rightProperty

  def right_=(n: Node) {
    right() = n
  }

  def bottom = delegate.bottomProperty

  def bottom_=(n: Node) {
    bottom() = n
  }

  def left = delegate.leftProperty

  def left_=(n: Node) {
    left() = n
  }

  def center = delegate.centerProperty

  def center_=(n: Node) {
    center() = n
  }
}