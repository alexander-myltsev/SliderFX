package model

case class News(content: String)

object InformationProvider {
  def getNews(): List[News] = {
    List(
      News("News1"),
      News("News2"),
      News("News3"),
      News("News4")
    )
  }

  def sentQuestion(question: String): Unit = {
    println("INFO: InformationProvider. Question sent: " + question)
  }
}