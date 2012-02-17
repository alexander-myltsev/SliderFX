package controller

import java.awt.image.BufferedImage

case class LectureDescription(id: Int, information: String, content: BufferedImage)

case class SlideInfo(id: Int, title: String, path: String, content: BufferedImage) {
  // NOTE: path is for mail sending. Remove it
}

case class RssChannel(name: String, items: Seq[RssItem])

case class RssItem(title: String, description: String, link: String)