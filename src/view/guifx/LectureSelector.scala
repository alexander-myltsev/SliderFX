package guifx

import controller._

import scalafx.Includes._
import scalafx.stage.Stage
import scalafx.scene.control.Label
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color
import scalafx.beans.property.{ReadOnlyDoubleProperty, DoubleProperty, IntegerProperty}
import scalafx.scene.layout.{VBox, HBox, BorderPane}
import scalafx.scene.control.Button

import scalafx.scene.image._
import scalafx.scene.layout.GridPane

import javafx.beans.property.{SimpleIntegerProperty, SimpleDoubleProperty}
import javafx.scene.web.HTMLEditor

class LeftSider extends VBox {

}

class RightSider extends VBox {
  val l1 = new Label {
    text = "label 1"
  }
  val l2 = new Label {
    text = "label 1"
  }
  content = List(l1, l2)
}

class LectureSelector extends BorderPane {
  val currentLecture = new SimpleIntegerProperty(0)

  val thumbnailHeight = new SimpleDoubleProperty(50.0)
  val thumbnailWidth = new SimpleDoubleProperty(50.0)

  val header_height = new SimpleDoubleProperty(5.0)
  val sider_width = new SimpleDoubleProperty(200.0)

  val w = new SimpleIntegerProperty(0)
  val h = new SimpleIntegerProperty(0)

  val lecturesDescriptions = {
    val cmd = new GetLecturesDescriptions
    Controller.executeCommand(cmd)
    cmd.lecturesDescriptions
  }

  bottom = new Rectangle {
    width <== w
    height <== header_height
    fill = Color.web("#0000FF")
  }

  top = new Rectangle {
    width <== w
    height <== header_height
    fill = Color.web("#0000FF")
  }

  center = new GridPane {
    content = List((new ImageView {
      image = lecturesDescriptions(0).previewPath
      fitWidth <== w - sider_width * 2.0
      fitHeight <== h - header_height * 2.0
    }, 0, 0),
      (new Button {
        text = "|>"
        translateX <== w - sider_width * 2.0 - 70
        translateY <== h / 2.0 - header_height - 70
      }, 0, 0))
  }

  left = new Rectangle {
    width <== sider_width
    height <== h - header_height * 2.0
    fill = Color.web("#00FF00") //Color.web("#00CED1") //Color.DARKTURQUOISE
  }

  //  right = new VBox {
  //    val l1 = new HTMLEditor()
  //    val l2 = new Button("Send")
  //    content = List(l1, l2)
  //  }

  //  center = new Rectangle {
  //    width <== w - sider_width * 2.0
  //    height <== h - header_height * 2.0
  //    fill = Color.web("#FF0000") //Color.web("#008B8B") //Color.DARKCYAN
  //  }

  right = new Rectangle {
    width <== sider_width
    height <== h - header_height * 2.0
    fill = Color.web("#00FF00") //Color.web("#00CED1") //Color.DARKTURQUOISE
  }
}