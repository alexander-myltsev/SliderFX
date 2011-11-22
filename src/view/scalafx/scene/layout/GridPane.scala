package scalafx.scene.layout

import javafx.scene.{layout => jfxsl}
import scalafx.Includes._
import scalafx.util.SFXDelegate
import javafx.geometry.Pos
import scalafx.scene.Node

object GridPane {
  implicit def sfxGridPane2jfx(v: GridPane) = v.delegate
}

class GridPane(override val delegate: jfxsl.GridPane = new jfxsl.GridPane()) extends Pane with SFXDelegate[jfxsl.GridPane] {
  def content_=(c: List[(Node, Int, Int)]) {
    delegate.getChildren.clear
    c foreach (x => delegate.add(x._1, x._2, x._3))
  }

  def columnConstraints = delegate.getColumnConstraints
  def rowConstraints = delegate.getRowConstraints
}