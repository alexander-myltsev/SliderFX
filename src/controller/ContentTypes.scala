package controller

import java.awt.image.BufferedImage

case class LectureDescription(id: Int, information: String, content: BufferedImage)

case class SlideInfo(id: Int, title: String, content: BufferedImage)