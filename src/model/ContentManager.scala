package model.lectures

import controller.{SlideInfo, LectureDescription}
import javax.imageio.ImageIO
import scala.xml._
import java.io.{FileReader, File}

case class Slide(path: String, sound: String)

case class Lecture(path: String, slides: Seq[Slide])

object ContentManager {
  private def overlayImages(bgImage: BufferedImage, fgImage: BufferedImage): BufferedImage = {
    if (fgImage.getHeight > bgImage.getHeight || fgImage.getWidth > fgImage.getWidth) {
      JOptionPane.showMessageDialog(null, "Foreground Image Is Bigger In One or Both Dimensions" + "\nCannot proceed with overlay." + "\n\n Please use smaller Image for foreground")
      return bgImage
    }
    val g: Graphics2D = bgImage.createGraphics
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.drawImage(bgImage, 0, 0, null)
    val alpha: Float = 0.7f
    val composite: AlphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)
    g.setComposite(composite)
    g.drawImage(fgImage, 350, 200, null)
    g.dispose
    bgImage
  }

  def getLectureDescription(lectureNumber: Int): LectureDescription = {
    val previewPath = "resource/Lectures/Lecture" + lectureNumber + "/Slide1.PNG"
    val content = ImageIO.read(new File(previewPath))
    val contentWithPlay = overlayImages(content, ImageIO.read(new File("resource/button_play_big.png")))
    new LectureDescription(lectureNumber, "Lecture " + lectureNumber, contentWithPlay)
  }

  def getLecturesDescriptions: List[LectureDescription] = {
    val lds = for (lectureNum <- 1 to 4) yield {
      getLectureDescription(lectureNum)
    }
    lds.toList
  }

  def getSlideInfo(lectureNum: Int, slideNumber: Int): SlideInfo = {
    val previewPath = "resource/Lectures/Lecture" + lectureNum + "/Slide" + slideNumber + ".PNG"
    val title = "Slide " + slideNumber
    val content = ImageIO.read(new File(previewPath))
    new SlideInfo(slideNumber, title, content)
  }

  def getSlidesInfo(lectureNum: Int): List[SlideInfo] = {
    val slds = for (slideNum <- 1 to 15) yield {
      getSlideInfo(lectureNum, slideNum)
    }
    slds.toList
  }

  def parseManifest(path:String) = {
    val xml = XML.load(new FileReader(path))
    val course = xml \\ "course"
    val lectures = (course \\ "lecture").map(lecture => {
      val path = (lecture \ "@path").text
      val slides = (lecture \\ "slide").map(slide => {
        val path = (slide \ "@path").text
        val sound = (slide \ "@sound").text
        Slide(path, sound)
      })
      Lecture(path, slides)
    })
    val lArr = lectures.toArray
    println(lArr)
  }
}