package controller

import java.awt.image.BufferedImage

case class LectureDescription(id: Int, information: String, content: BufferedImage)

case class SlideInfo(id: Int, title: String, content: BufferedImage)

case class RssChannel(name: String, items: Seq[RssItem])

case class RssItem(title: String, description: String, link: String)