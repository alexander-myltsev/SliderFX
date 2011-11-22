package model.lectures

case class LectureDescription(information: String, previewPath: String)

object LectureManager {
  def getLecturesDescriptions(): List[LectureDescription] = {
    val lds = for (lectureNum <- 1 to 4) yield {
      val previewPath = "resource/Lectures/Lecture" + lectureNum + "/Slide1.PNG"
      new LectureDescription("Lecture " + lectureNum, previewPath)
    }
    lds.toList
  }
}