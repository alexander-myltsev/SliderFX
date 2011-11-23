package model.lectures

case class LectureDescription(id: Int, information: String, previewPath: String)

case class SlideInfo(id: Int, title: String, previewPath: String)

object LectureManager {
  def getLecturesDescriptions: List[LectureDescription] = {
    val lds = for (lectureNum <- 1 to 4) yield {
      val previewPath = "resource/Lectures/Lecture" + lectureNum + "/Slide1.PNG"
      new LectureDescription(lectureNum, "Lecture " + lectureNum, previewPath)
    }
    lds.toList
  }

  def getSlidesInfo(lectureNum: Int): List[SlideInfo] = {
    val slds = for (slideNum <- 1 to 23) yield {
      val previewPath = "resource/Lectures/Lecture" + lectureNum + "/Slide" + slideNum + ".PNG"
      val title = "Slide " + slideNum
      new SlideInfo(slideNum, title, previewPath)
    }
    slds.toList
  }
}