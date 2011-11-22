package gui

import javafx.beans.value._
import javafx.event._
import scalafx.scene.control.Label
import scalafx.scene.layout.BorderPane._
import scalafx.scene.shape.Rectangle
import scalafx.scene.shape.Shape._
import scalafx.scene.paint.Color

//import javafx.scene.control._

import javafx.scene.image._

//import javafx.scene.layout._

import javafx.scene.input._
import javafx.scene.media._
import javafx.util._

import scalafx.application.JFXApp
import scalafx.stage.Stage
import kioskfx._
import scalafx.stage.Stage._
import scalafx.scene.{Group, Scene}
import scalafx.scene.layout._

abstract class Cmd

case class Authorize() extends Cmd

case class LecturesSelect() extends Cmd

case class SlidesSelect(lectureNum: Int) extends Cmd

class ViewManager(group: Group,
                  widthProperty: javafx.beans.property.ReadOnlyDoubleProperty,
                  heightProperty: javafx.beans.property.ReadOnlyDoubleProperty) {

  def authorize(): Unit = {
    /*
    val gp = new javafx.scene.layout.GridPane
    gp.add(new Label("Code to send   "), 0, 0)
    gp.add(new TextField("1234567"), 1, 0)
    gp.add(new Label("Enter accepted code here   "), 0, 1)
    gp.add(new TextField(), 1, 1)
    def handler() = {
      fill(LecturesSelect())
    }
    val bt = new Button("Send")
    bt.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent) {
        handler()
      }
    })

    gp.add(bt, 1, 2)
    group.children.add(gp)

    */

    val gp = new GridPane {
      content = List(
        (new Label {
          text = "Code to send   "
        }, 0, 0),
        (new Label {
          text = "Enter accepted code here   "
        }, 0, 1))
    }
    group.children.add(gp)
  }

  def lecturesSelector(): Unit = {
    /*
    val menu =
      new KiMenu()
        .width(widthProperty)
        .height(heightProperty)

    for (lectureNum <- 1 to 4) {
      val path = "resource/Lectures/Lecture" + lectureNum + "/Slide1.PNG"
      val img = new Image(path)
      def handle = fill(SlidesSelect(lectureNum))
      menu.section(new KiSection()
        .image(img)
        .action(new KiAction()
        .image(img)
        .onSelect(new KiJob {
        override def start() {
          //println("lecture " + lectureNum + " selected")
          handle
        }
      })))
    }

    group.children.clear
    group.children.add(menu.node)
    */

    /*
    val ls = new guifx.ContentViewer() {
      //width <== widthProperty
      //height <== heightProperty
    }
    */

    /*
    val layout = new BorderPane();
    layout.setTop(new Rectangle(200, 50, Color.DARKCYAN));
    layout.setBottom(new Rectangle(200, 50, Color.DARKCYAN));
    layout.setCenter(new Rectangle(100, 100, Color.MEDIUMAQUAMARINE));
    layout.setLeft(new Rectangle(50, 100, Color.DARKTURQUOISE));
    layout.setRight(new Rectangle(50, 100, Color.DARKTURQUOISE));

    group.children.clear
    group.children.add(ls)
    */
  }

  def slidesSelector(lectureNum: Int): Unit = {
    val menu =
      new KiMenu()
        .width(widthProperty)
        .height(heightProperty)

    for (slideNum <- 1 to 23) {
      val path = "resource/Lectures/Lecture" + lectureNum + "/Slide" + slideNum + ".PNG"
      val img = new Image(path)
      def handle = fill(SlidesSelect(lectureNum))
      menu.section(new KiSection()
        .image(img)
        .action(new KiAction()
        .image(img)
        .onSelect(new KiJob {
        override def start() {
          println("lecture " + slideNum + " selected")
          //handle
        }
      })))
    }

    // -----------------------------------------
    val mediaPlayer = new MediaPlayer(new Media("file:///E:/temp/music/oow2010.flv"))
    //val mediaPlayer = new MediaPlayer(new Media("http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv"))
    val mediaView = new MediaView(mediaPlayer)

    val playButton = new javafx.scene.control.Button("|>")
    playButton.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent) {
        mediaPlayer.play()
      }
    })

    val pauseButton = new javafx.scene.control.Button("||")
    pauseButton.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent) {
        mediaPlayer.pause()
      }
    })

    val seekSlider = new javafx.scene.control.Slider
    seekSlider.setMax(100)
    seekSlider.valueProperty.addListener(new ChangeListener[Number] {
      override def changed(o: ObservableValue[_ <: Number], oldVal: Number, newVal: Number): Unit = {
        val startTimeMills = mediaPlayer.getStartTime.toMillis
        val stopTimeMills = mediaPlayer.getStopTime.toMillis
        val seekTimeMills = (stopTimeMills - startTimeMills) * seekSlider.getValue / seekSlider.getMax // assume slider.getMin == 0
        mediaPlayer.seek(new Duration(seekTimeMills))

        val slideNum = (seekSlider.getValue / seekSlider.getMax * 22.0).toInt
        println(slideNum)
        menu.currentSection1(slideNum)
      }
    })

    val volumeSlider = new javafx.scene.control.Slider
    volumeSlider.setValue(volumeSlider.getMax)
    volumeSlider.valueProperty.addListener(new ChangeListener[Number] {
      override def changed(o: ObservableValue[_ <: Number], oldVal: Number, newVal: Number): Unit = {
        mediaPlayer.setVolume(volumeSlider.getValue / volumeSlider.getMax)
      }
    })

    val hbox2 = new HBox
    hbox2.setTranslateX(250)
    hbox2.setTranslateY(540)
    hbox2.getChildren.addAll(playButton, pauseButton, seekSlider, volumeSlider)

    menu.node.getChildren.add(hbox2)
    // -----------------------------------------

    group.children.clear
    group.children.add(menu.node)
  }

  def fill(request: Cmd): Unit = request match {
    case (cmd: Authorize) => authorize
    case (cmd: LecturesSelect) => lecturesSelector
    case (cmd: SlidesSelect) => slidesSelector(cmd.lectureNum)
  }
}

object Main extends JFXApp {
  stage = new Stage {
    width = 1200
    height = 800
    title = "GUI prototype"

    scene = new Scene {

    }
  }

  //stage.setFullScreen(true)

  //val vm = new ViewManager(group, stage.widthProperty, stage.heightProperty)
  //vm.fill(AuthorizeCmd())
  //vm.fill(LecturesSelect())
  //vm.fill(SlidesSelect(1))
}








