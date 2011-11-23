package guifx

import controller._

import scalafx.Includes._
import scalafx.stage.Stage
import scalafx.scene.Node
import scalafx.scene.control.Label
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color
import scalafx.beans.property.{ReadOnlyDoubleProperty, DoubleProperty, IntegerProperty}
import scalafx.scene.layout.{VBox, HBox, BorderPane}
import scalafx.scene.control.Button
import scalafx.scene.image._
import scalafx.scene.layout.GridPane
import scalafx.scene.control.TextArea

import javafx.beans.property.{SimpleIntegerProperty, SimpleDoubleProperty}
import scalafx.scene.layout.BorderPane._
import javafx.scene.input.MouseEvent
import javafx.scene.web.HTMLEditor
import javafx.scene.layout.{RowConstraints, ColumnConstraints}

class LecturesViewer extends BorderPane {
  def selectLecture(lectureNum: Int) = {
    // TODO: simplify centralImage.image.value
    val path = lecturesDescriptions.find(_.id == lectureNum) match {
      case Some(x) => x.previewPath
      case None => throw new Exception("Lecture with id == " + lectureNum + " is not found.")
    }
    centralImage.image.value = new javafx.scene.image.Image(path)
  }

  val thumbnailHeight = 150.0
  val thumbnailWidth = 150.0
  val thumbnailSpacing = 10.0

  val header_height = new SimpleDoubleProperty(5.0)
  val sider_width = new SimpleDoubleProperty(200.0)

  val w = new SimpleIntegerProperty(0)
  val h = new SimpleIntegerProperty(0)

  val lecturesDescriptions = {
    val cmd = new GetLecturesDescriptionsCmd
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

  //    left = new Rectangle {
  //      width <== sider_width
  //      height <== h - header_height * 2.0
  //      fill = Color.web("#00FF00") //Color.web("#00CED1") //Color.DARKTURQUOISE
  //    }

  val centralImage = new ImageView {
    image = lecturesDescriptions.find(_.id == 1) match {
      case Some(x) => x.previewPath
      case None => throw new Exception("Lecture with id == 1 is not found.")
    }
    fitWidth <== w - sider_width * 2.0
    fitHeight <== h - header_height * 2.0
  }

  left = new VBox {
    //      def mouseClickedHandler(x: MouseEvent) = {
    //        println("DEBUG: Lecture selected " + x)
    //        //Controller.executeCommand(new GoForwardCmd)
    //      }

    content = for (ls <- lecturesDescriptions) yield {
      new ImageView {
        image = ls.previewPath
        fitWidth = thumbnailWidth
        fitHeight = thumbnailHeight

        onMouseClicked = ((x: MouseEvent) => {
          // TODO: move this multiple instanced handler to one handler
          println("DEBUG: lecture selected - " + ls.id)
          Controller.executeCommand(new SelectLectureCmd(ls.id))
        })
      }
    }
    spacing = thumbnailSpacing
  }

  center = new GridPane {
    content = List((centralImage, 0, 0),
      (new Button {
        text = "|>"
        translateX <== w - sider_width * 2.0 - 70
        translateY <== h / 2.0 - header_height - 70
        onMouseClicked = ((x: MouseEvent) => {
          println("DEBUG: |> - View slides")
          Controller.executeCommand(new GoForwardCmd)
        })
      }, 0, 0))
  }

  // TODO: Fix bug when goback from slides
  val gp = new GridPane {
    content = List((new TextArea, 0, 0),
      (new Button {
        text = "Send question"
        translateX = 100
        translateY = 150
        onMouseClicked = mouseClickedHandler((_: MouseEvent))

        def mouseClickedHandler(x: MouseEvent) = {
          println("DEBUG: Question sent")
        }
      }, 0, 0))
  }

  // TODO: Simplify it
  val rc = new RowConstraints()
  rc.setMinHeight(300)
  gp.rowConstraints.addAll(rc)
  right = gp
}

class SlidesViewer extends BorderPane {
  val thumbnailHeight = 150.0
  val thumbnailWidth = 150.0
  val thumbnailSpacing = 10.0

  val header_height = new SimpleDoubleProperty(5.0)
  val sider_width = new SimpleDoubleProperty(200.0)

  val w = new SimpleIntegerProperty(0)
  val h = new SimpleIntegerProperty(0)

  val slidesInfo = {
    val cmd = new GetSlidesCmd(1)
    Controller.executeCommand(cmd)
    cmd.slidesInfo
  }

  center = new GridPane {
    content = List((new ImageView {
      image = slidesInfo(1).previewPath
      fitWidth <== w - sider_width * 2.0
      fitHeight <== h - header_height * 2.0
    }, 0, 0),
      (new Button {
        text = "<|"
        translateX = 70
        translateY <== h / 2.0 - header_height - 70
        onMouseClicked = mouseClickedHandler((_: MouseEvent))

        def mouseClickedHandler(x: MouseEvent) = {
          println("<| =====> View lectures")
          Controller.executeCommand(new GoBackCmd)
        }
      }, 0, 0))
  }

  right = new Rectangle {
    width <== sider_width
    height <== h - header_height * 2.0
    fill = Color.web("#00FF00") //Color.web("#00CED1") //Color.DARKTURQUOISE
  }

  left = new Rectangle {
    width <== sider_width
    height <== h - header_height * 2.0
    fill = Color.web("#00FF00") //Color.web("#00CED1") //Color.DARKTURQUOISE
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

}