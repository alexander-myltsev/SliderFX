package model.lectures

import _root_.controller.{SlideInfo, LectureDescription}

object ContentManager {
  def getLecturesDescriptions: List[LectureDescription] = {
    val lds = for (lectureNum <- 1 to 4) yield {
      val previewPath = "resource/Lectures/Lecture" + lectureNum + "/Slide1.PNG"
      new LectureDescription(lectureNum, "Lecture " + lectureNum, previewPath)
    }
    lds.toList
  }

  def getSlidesInfo(lectureNum: Int): List[SlideInfo] = {
    val slds = for (slideNum <- 1 to 15) yield {
      val previewPath = "resource/Lectures/Lecture" + lectureNum + "/Slide" + slideNum + ".PNG"
      val title = "Slide " + slideNum
      new SlideInfo(slideNum, title, previewPath)
    }
    slds.toList
  }
}