package model.lectures

import controller.{SlideInfo, LectureDescription}
import javax.imageio.ImageIO
import java.io.File

object ContentManager {
  def getLectureDescription(lectureNumber: Int): LectureDescription = {
    val previewPath = "resource/Lectures/Lecture" + lectureNumber + "/Slide1.PNG"
    val content = ImageIO.read(new File(previewPath))
    new LectureDescription(lectureNumber, "Lecture " + lectureNumber, content)
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