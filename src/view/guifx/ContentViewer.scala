package guifx

import _root_.controller._
import scalafx.Includes._
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color
import scalafx.scene.layout.{VBox, HBox, BorderPane}
import scalafx.scene.image._
import scalafx.scene.layout.GridPane
import javafx.beans.property.{SimpleIntegerProperty, SimpleDoubleProperty}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{RowConstraints, ColumnConstraints}
import scalafx.scene.control.{Slider, Button, TextArea}
import javafx.scene.control.Tooltip
import scalafx.scene.control.Labeled._

class Viewer(controller: Controller) extends ViewerAbstract {
  private val viewer = this // TODO: fix it

  def setLecture(lectureNum: Int) = {
    content = lectureViewer()
  }

  def setSlide(slideNum: Int) = {
    content = slidesViewer()
  }

  def updateNews(news: List[String]) = {
    println("Viewer.updateNews")
  }

  val header_height = new SimpleDoubleProperty(5.0)
  val sider_width = new SimpleDoubleProperty(200.0)

  val w = new SimpleIntegerProperty(1350)
  val h = new SimpleIntegerProperty(800)

  def w_=(v: Int) = w() = v

  def h_=(v: Int) = h() = v

  private val sendMsg = new TextArea {
    val msg = "Type your question or query here and click \"send\" to receive \na consultation"
    text = msg
    tooltip = new Tooltip(msg)
    translateY <== h - this.height - 40.0
    maxWidth <== sider_width * 2.0 - 10

    onMouseEntered = ((x: MouseEvent) => {
      if (this.text() == msg) this.text = ""
    })

    onMouseExited = ((x: MouseEvent) => {
      if (this.text() == "") this.text = msg
    })
  }

  var content: BorderPane = lectureViewer()

  private def lectureViewer() = new BorderPane {
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

    val lecturesDescriptions = {
      val cmd = new GetLecturesDescriptionsCmd
      controller.executeCommand(viewer, cmd)
      cmd.lecturesDescriptions
    }

    bottom = new HBox {
      content = for (ls <- lecturesDescriptions) yield {
        new Button {
          minWidth = 150
          text = ls.information

          onMouseClicked = ((x: MouseEvent) => {
            // TODO: move this multiple instanced handler to one handler
            println("DEBUG: lecture selected - " + ls.id)
            controller.executeCommand(viewer, new SelectLectureCmd(ls.id))
          })
        }
      }

      translateX = 40
      translateY = 5
      spacing = thumbnailSpacing
    }

    top = new Rectangle {
      width <== w
      height <== header_height
      fill = Color.web("#0000FF")
    }

    val centralImage = new ImageView {
      image = lecturesDescriptions.find(_.id == 1) match {
        case Some(x) => x.previewPath
        case None => throw new Exception("Lecture with id == 1 is not found.")
      }
      fitWidth <== w - sider_width * 2.0
      fitHeight <== h - header_height * 2.0 - 30
    }

    /*
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
    */

    center = new GridPane {
      content = List((centralImage, 0, 0),
        (new ImageView {
          //text = "|>"
          translateX <== w / 2.0 - sider_width - 120
          translateY <== -50 //h / 2.0 - header_height - 70
          opacity = 0.7

          image = "resource/Silver-Play-Button.jpg"

          maxWidth = 10
          maxHeight = 10

          onMouseClicked = ((x: MouseEvent) => {
            println("DEBUG: |> - View slides")
            controller.executeCommand(viewer, new GoForwardCmd)
          })
        }, 0, 0))
    }

    // TODO: Fix bug when goback from slides
    val gp = new GridPane {
      content = List(
        (sendMsg, 0, 0),
        (new Button {
          text = "Send"
          translateX = 320
          translateY <== h - 190
          onMouseClicked = ((x: MouseEvent) => {
            println("DEBUG: Question is sent")
          })
        }, 0, 0),
        (new ImageView {
          image = "resource/NV_CUDAZONE.jpg"
          fitHeight = 200
          translateY = 0
        }, 0, 0),
        (new TextArea {
          text = """
News #1
News #2
News #3
"""
          translateY = 220
          minHeight = 250
        }, 0, 0),
        (new HBox {
          content = List(
            new Button {
              //text = "Twitter"
              graphic = new ImageView {
                image = "resource/Twitter-icon.png"
                fitHeight = 30
                fitWidth = 30
              }
              maxWidth = 30
              maxHeight = 30
            },
            new Button {
              //text = "Facebook"
              graphic = new ImageView {
                image = "resource/facebook-icon.png"
                fitHeight = 30
                fitWidth = 30
              }
              maxWidth = 30
              maxHeight = 30
            }
            /*,
            new Button {
              text = "LinkedIn"
            },
            new Button {
              text = "Youtube"
            },
            new Button {
              text = "RSS"
            }*/
          )

          translateY <== h - 35
          translateX <== 300
        }, 0, 0))
    }

    // TODO: Simplify it
    val rc = new RowConstraints()
    rc.setMinHeight(150)
    gp.rowConstraints.addAll(rc)
    right = gp
  }

  private def slidesViewer() = new BorderPane {
    def selectSlide(slideNum: Int): Unit = {
      // TODO: simplify centralImage.image.value
      val path = slidesInfo.find(_.id == slideNum) match {
        case Some(x) => x.previewPath
        case None => throw new Exception("Slide with id == " + slideNum + " is not found.")
      }
      centralImage.image.value = new javafx.scene.image.Image(path)
    }

    val thumbnailHeight = 20.0
    val thumbnailWidth = 150.0
    val thumbnailSpacing = 5.0

    val slidesInfo = {
      val cmd = new GetSlidesCmd(1)
      controller.executeCommand(viewer, cmd)
      cmd.slidesInfo
    }

    val centralImage = new ImageView {
      image = slidesInfo.head.previewPath
      fitWidth <== w - sider_width * 2.0
      fitHeight <== h - header_height * 2.0 - 20
    }

    val playPauseBtn: Button = new Button {
      var isPlaying = true
      text = "||"
      minWidth = 35
      onMouseClicked = ((x: MouseEvent) => {
        if (isPlaying) playPauseBtn.setText("|>")
        else playPauseBtn.setText("||")
        isPlaying = !isPlaying
      })
    }

    val hbox = new HBox {
      content = List(
        playPauseBtn,
        new Slider {
          maxWidth = 600
          minWidth = 600
          translateX = 20
        },
        new Button {
          graphic = new ImageView {
            image = "resource/volume+icon.jpg"
            fitHeight = 20
            fitWidth = 20
          }
          translateX = 60
        },
        new Slider {
          maxWidth = 100
          translateX = 70
        }
      )
    }

    center = new GridPane {
      content = List((new VBox {
        content = List(
          centralImage,
          hbox)
      }, 0, 0),
        (new Button {
          text = "<|"
          translateX = -130
          translateY <== h / 2.0 - header_height - 10
          onMouseClicked = mouseClickedHandler((_: MouseEvent))

          def mouseClickedHandler(x: MouseEvent) = {
            println("<| =====> View lectures")
            controller.executeCommand(viewer, new GoBackCmd)
          }
        }, 0, 0))
    }

    left = new VBox {
      //      def mouseClickedHandler(x: MouseEvent) = {
      //        println("DEBUG: Lecture selected " + x)
      //        //Controller.executeCommand(new GoForwardCmd)
      //      }

      def update(si: SlideInfo): Unit = {
        buttons foreach ((b: Button) => {
          b.minHeight.set(thumbnailHeight)
          b.graphic() = null
        })
        for (i <- si.id - 3 to si.id + 1) {
          if (i >= 0 && i < buttons.length) {
            val button = buttons(i)
            button.setMinHeight(thumbnailHeight + 50)
            button.graphic() = new ImageView {
              image = si.previewPath
              fitWidth = 70
              fitHeight = 70
            }.delegate
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

    // TODO: remove code duplication with LectureViewer
    // TODO: Fix bug when goback from slides
    val gp = new GridPane {
      content = List(
        (sendMsg, 0, 0))
    }

    // TODO: Simplify it
    val rc = new RowConstraints()
    rc.setMinHeight(150)
    gp.rowConstraints.addAll(rc)
    right = gp
  }
}