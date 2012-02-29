package controller

import java.awt.image.BufferedImage
import java.net.URL

case class LectureDescription(id: Int, information: String, content: BufferedImage)

case class SlideInfo(id: Int, title: String, fullTitle: String, path: String, content: BufferedImage, mediaURL: URL) {
  // NOTE: path is for mail sending. Remove it
}

case class RssChannel(name: String, items: Seq[RssItem])

case class RssItem(title: String, description: String, link: String)

case class Contacts(fullname: String, organization: String, email: String)