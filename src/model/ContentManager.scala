package model.lectures

import controller.{SlideInfo, LectureDescription}
import javax.imageio.ImageIO
import java.io.File
import javax.swing.JOptionPane
import java.awt.{AlphaComposite, RenderingHints, Graphics2D}
import java.awt.image.BufferedImage

object ContentManager {
  private def overlayImages(bgImage: BufferedImage, fgImage: BufferedImage): BufferedImage = {
    if (fgImage.getHeight > bgImage.getHeight || fgImage.getWidth > fgImage.getWidth) {
      JOptionPane.showMessageDialog(null, "Foreground Image Is Bigger In One or Both Dimensions" + "\nCannot proceed with overlay." + "\n\n Please use smaller Image for foreground")
      return bgImage
    }
    val g: Graphics2D = bgImage.createGraphics
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.drawImage(bgImage, 0, 0, null)
    val alpha: Float = 0.2f
    val composite: AlphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)
    g.setComposite(composite)
    g.drawImage(fgImage, 350, 200, null)
    g.dispose
    bgImage
  }

  def getLectureDescription(lectureNumber: Int): LectureDescription = {
    val previewPath = "resource/Lectures/Lecture" + lectureNumber + "/Slide1.PNG"
    val content = ImageIO.read(new File(previewPath))
    val contentWithPlay = overlayImages(content, ImageIO.read(new File("resource/Silver-Play-Button.jpg")))
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
}