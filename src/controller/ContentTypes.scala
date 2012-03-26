package controller

import java.awt.image.BufferedImage
import java.net.URL

class LectureDescription(information: String, content: => BufferedImage) {
  def getContent = content

  def getInformation = information
}

class SlideInfo(id: Int, title: String, fullTitle: String, path: String, content: => BufferedImage, mediaURL: => URL) {
  // NOTE: path is for mail sending. Remove it

  def getId = id

  def getTitle = title

  def getFullTitle = fullTitle

  def getPath = path

  def getContent = content

  def getMediaURL = mediaURL
}

case class RssChannel(name: String, items: Seq[RssItem])

case class RssItem(title: String, description: String, link: String)

case class Contacts(fullname: String, organization: String, email: String)