package guifx

import _root_.controller._
import scalafx.Includes._
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color
import scalafx.scene.layout.{VBox, HBox, BorderPane}
import scalafx.scene.layout.GridPane
import scalafx.scene.control.{Slider, Button, TextArea}
import scalafx.scene.Scene
import scalafx.beans.property.{IntegerProperty}
import scalafx.geometry.Insets
import scalafx.scene.paint.Color._
import scalafx.scene.image._

import javafx.beans.property.{SimpleIntegerProperty}
import javafx.scene.input.MouseEvent
import javafx.scene.paint.{Stop, CycleMethod, RadialGradient}
import javafx.geometry.Pos

class Viewer(controller: Controller, scene: Scene) extends ViewerAbstract {
  viewer =>

  val fog = new Rectangle {
    val fogColor = Color.web("#000000")
    val dark: Color = Color.color(fogColor.getRed, fogColor.getGreen, fogColor.getBlue, 1.0)
    val light: Color = Color.color(fogColor.getRed, fogColor.getGreen, fogColor.getBlue, 0.5)
    val gr: RadialGradient = new RadialGradient(0.0, 0.0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, new Stop(0, light), new Stop(1, dark))

    width <== scene.width
    height <== scene.height
    fill = gr
    mouseTransparent = true
  }

  val lectureNumber = new IntegerProperty(new SimpleIntegerProperty(1))
  val slideNumber = new IntegerProperty(new SimpleIntegerProperty(1))
  var news = ""
  controller.executeCommand(viewer, new WatchNewsCmd)

  val socialButtons = new HBox {
    /*
    val twitterButton = new Button {
      graphic = new ImageView {
        image = "resource/Twitter-icon.png"
        fitHeight = 32
        fitWidth = 32
      }
      maxWidth = 32
      maxHeight = 32
    }
    val facebookButton = new Button {
      graphic = new ImageView {
        image = "resource/facebook-icon.png"
        fitHeight = 32
        fitWidth = 32
      }
      maxWidth = 32
      maxHeight = 32
    }
    */

    // TODO: Add LinkedIn, Youtube, RSS social buttons

    val twitterButton = new ImageView {
      image = "resource/Twitter-icon.png"
      fitHeight = 32
      fitWidth = 32
      onMouseClicked = ((x: MouseEvent) => {
        println("DEBUG: twitter button is clicked")
      })
    }

    val facebookButton = new ImageView {
      image = "resource/facebook-icon.png"
      fitHeight = 32
      fitWidth = 32
      onMouseClicked = ((x: MouseEvent) => {
        println("DEBUG: facebook button is clicked")
      })
    }

    content = List(twitterButton, facebookButton)
    alignment = Pos.BOTTOM_RIGHT
  }

  def updateNews(news: List[String]) = {
    viewer.news = news.mkString("\n\n")
  }

  def setLecture(lectureNum: Int) = {
    println("state: " + viewer.state)
    if (viewer.state != ViewerState.SelectingLecture) {
      println("!!!recreated!!!")
      decorateForLecturing(lectureNum)
      viewer.state = ViewerState.SelectingLecture
    }
    lectureNumber() = lectureNum
  }

  def setSlide(slideNum: Int) = {
    //println("state: " + viewer.state)
    if (viewer.state != ViewerState.SelectingSlides) {
      println("!!!recreated!!!")
      decorateForSliding(slideNum)
      viewer.state = ViewerState.SelectingSlides
    }
    slideNumber() = slideNum
  }

  val banner_width = 200.

  decorateForLecturing(1)

  def rightPane = new VBox {
    alignment = Pos.TOP_RIGHT

    val questionTextArea = new TextArea {
      padding = Insets(20, 0, 0, 0)

      prefHeight <== scene.height * 0.3
      val msg = "Type your question or query here and click \"send\" to receive a consultation"
      text = msg
      //tooltip = new Tooltip(msg)
      //maxWidth <== sider_width * 2.0 - 10

      onMouseEntered = ((x: MouseEvent) => {
        if (this.text() == msg) this.text = ""
      })

      onMouseExited = ((x: MouseEvent) => {
        if (this.text() == "") this.text = msg
      })

      prefWidth = banner_width
      wrapText = true
    }

    content = List(
      new ImageView {
        //image = ("resource/TESLA_20Series-WBA_200x100_pic.jpg", 200., 100.)
        image = "resource/TESLA_20Series-WBA_200x100_pic.jpg"
        onMouseClicked = ((x: MouseEvent) => {
          println("DEBUG: TESLA_20Series is clicked")
        })
        //fitWidth = 200
      },
      new TextArea {
        text = news
        prefWidth = banner_width
        prefHeight <== scene.height * 0.4
        wrapText = true
        editable = false
      },
      questionTextArea,
      new Button {
        text = "Send"
        translateX = -5
        translateY = -10
        onMouseClicked = ((x: MouseEvent) => {
          println("DEBUG: Question is sent: '" + questionTextArea.text() + "'")
        })
      })
  }

  def decorateForLecturing(lectureNum: Int) = {
    val content = new BorderPane {
      prefWidth <== scene.width
      prefHeight <== scene.height
    }
    scene.content = List(fog, content)

    val thumbnailSpacing = 10.

    val lecturesDescriptions = {
      val cmd = new GetLecturesDescriptionsCmd
      controller.executeCommand(viewer, cmd)
      cmd.lecturesDescriptions
    }

    content.center = new VBox {
      prefWidth <== scene.width - banner_width
      prefHeight <== scene.height
      spacing = 20.
      translateY = 20.
      translateX = 20.

      content = List(
        new GridPane {
          gridPane =>

          val centralImage = new ImageView {
            image = lecturesDescriptions.find(_.id == lectureNum) match {
              case Some(x) => x.previewPath
              case None => throw new Exception("Lecture with id == " + lectureNum + " is not found.")
            }
            fitWidth <== scene.width - banner_width - 40.
            fitHeight <== scene.height - 100.

            preserveRatio = true
          }

          lectureNumber onChange {
            (prop, oldVal, newVal) => {
              if (oldVal != newVal) {
                val path = lecturesDescriptions.find(_.id == newVal.intValue) match {
                  case Some(x) => x.previewPath
                  case None => throw new Exception("Lecture with id == " + lectureNum + " is not found.")
                }
                centralImage.image = path
              }
            }
          }

          content = List(
            centralImage,
            new ImageView {
              //translateX <== centralImage.image().widthProperty / 2.
              translateX = 20
              translateY = -20
              opacity = 0.2

              image = "resource/Silver-Play-Button.jpg"

              fitHeight = 100.
              fitWidth = 100.

              onMouseClicked = ((x: MouseEvent) => {
                println("DEBUG: View slides")
                controller.executeCommand(viewer, new GoForwardCmd)
              })
            })
        })
    }

    content.bottom = new VBox {
      prefWidth <== scene.width

      content = List(
        new HBox {
          translateX = 30.

          val lectureButtons = for (ls <- lecturesDescriptions) yield {
            new Button {
              minWidth = 150.
              text = ls.information

              onMouseClicked = ((x: MouseEvent) => {
                // TODO: move this multiple instanced handler to one handler
                println("DEBUG: lecture selected - " + ls.id)
                controller.executeCommand(viewer, new SelectLectureCmd(ls.id))
              })
            }
          }

          content = lectureButtons
          alignment = Pos.BOTTOM_LEFT
          spacing = thumbnailSpacing
        },
        socialButtons)

      padding = Insets(5, 5, 5, 5)
    }

    content.right = rightPane
  }

  def decorateForSliding(slideNum: Int) = {
    val thumbnailHeight = 20.0
    val thumbnailWidth = 150.0
    val thumbnailSpacing = 5.0

    val content = new BorderPane {
      prefWidth <== scene.width
      prefHeight <== scene.height
    }
    scene.content = List(fog, content)

    val slidesInfo = {
      val cmd = new GetSlidesCmd(lectureNumber())
      controller.executeCommand(viewer, cmd)
      cmd.slidesInfo
    }

    val centralImage = new ImageView {
      image = slidesInfo.head.previewPath

      fitWidth <== scene.width - banner_width - thumbnailWidth - 40.
      fitHeight <== scene.height - 70.

      preserveRatio = true
    }

    /*
    def selectSlide(slideNum: Int): Unit = {
      // TODO: simplify centralImage.image.value
      val path = slidesInfo.find(_.id == slideNum) match {
        case Some(x) => x.previewPath
        case None => throw new Exception("Slide with id == " + slideNum + " is not found.")
      }
      centralImage.image.value = new javafx.scene.image.Image(path)
    }
    */

    slideNumber onChange {
      (prop, oldVal, newVal) => {
        if (oldVal != newVal) {
          val path = slidesInfo.find(_.id == newVal.intValue) match {
            case Some(x) => x.previewPath
            case None => throw new Exception("Slide with id == " + slideNum + " is not found.")
          }
          centralImage.image = path
        }
      }
    }

    val slideNavigationBox = new HBox {
      padding = Insets(5, 5, 5, 5)
      prefWidth <== scene.width - thumbnailWidth - banner_width

      content = List(
        new Button {
          text = "Lectures\nselection"

          //translateY <== h / 2.0 - header_height - 10
          onMouseClicked = mouseClickedHandler((_: MouseEvent))

          def mouseClickedHandler(x: MouseEvent) = {
            println("<| =====> View lectures")
            controller.executeCommand(viewer, new GoBackCmd)
          }
        },
        new Button {
          translateX = thumbnailWidth

          var isPlaying = true
          text = "||"
          minWidth = 35
          onMouseClicked = ((x: MouseEvent) => {
            if (isPlaying) this.text() = "|>"
            else this.text() = "||"
            isPlaying = !isPlaying
          })
        },
        new Slider {
          prefWidth = 400
          translateX = thumbnailWidth + 20
        },
        new Button {
          graphic = new ImageView {
            image = "resource/volume+icon.jpg"
            fitHeight = 15
            fitWidth = 15
          }
          translateX = thumbnailWidth + 60
        },
        new Slider {
          prefWidth = 100
          translateX = thumbnailWidth + 70
        }
      )
    }

    content.center = new GridPane {
      padding = Insets(20, 0, 0, 20)

      content = List(centralImage)
    }

    content.left = new VBox {
      padding = Insets(20, 0, 0, 10)

      //      def mouseClickedHandler(x: MouseEvent) = {
      //        println("DEBUG: Lecture selected " + x)
      //        //Controller.executeCommand(new GoForwardCmd)
      //      }

      def update(si: SlideInfo): Unit = {
        val pointer = "--->"
        buttons foreach ((b: Button) => {
          b.minHeight() = thumbnailHeight
          b.graphic() = null // TODO: All except two are actually non-null

          val txt = b.text()
          if (txt.endsWith(pointer)) b.text() = txt.substring(0, txt.length - pointer.length)
        })
        buttons(si.id - 1).text() += pointer
        //for (i <- si.id - 3 to si.id + 1) {
        for (i <- si.id - 2 to si.id) {
          if (i >= 0 && i < buttons.length && i + 1 != si.id) {
            val button = buttons(i)
            button.setMinHeight(thumbnailHeight + 50)
            button.graphic = new ImageView {
              image = slidesInfo(i).previewPath // TODO: fix it
              fitWidth = 70
              fitHeight = 70
            }
          }
        }
      }

      val buttons: List[Button] = for (si <- slidesInfo) yield {
        new Button {
          text = si.title
          minWidth = thumbnailWidth

          onMouseClicked = ((x: MouseEvent) => {
            // TODO: move this multiple instanced handler to one handler
            val selectedSlide = si.id
            println("DEBUG: slide selected - " + selectedSlide)
            controller.executeCommand(viewer, new SelectSlideCmd(selectedSlide))
            update(si)
          })
        }
      }

      content = buttons
      spacing = thumbnailSpacing
    }

    content.bottom = new VBox {
      padding = Insets(5, 5, 5, 5)

      content = List(
        slideNavigationBox,
        socialButtons
      )
    }

    content.right = rightPane
  }
}