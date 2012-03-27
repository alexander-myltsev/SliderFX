package model

import controller.{SlideInfo, LectureDescription}
import javax.imageio.ImageIO
import scala.xml._
import scala.xml._
import java.io.{InputStream, FileOutputStream, File}
import java.net.URL

// TODO: fix temp directory construction
// win: tempDir + "tmp"
// nix: tempDir + "/tmp"
// public static String combine (String path1, String path2)
// {
//   File file1 = new File(path1);
//   File file2 = new File(file1, path2);
//   return file2.getPath();
// }

case class Slide(path: String, sound: String)

case class Lecture(path: String, slides: Seq[Slide])

class ContentManager(aesEncrypter: AesEncrypter) {
  private def overlayImages(bgImage: BufferedImage, fgImage: BufferedImage): BufferedImage = {
    if (fgImage.getHeight > bgImage.getHeight || fgImage.getWidth > fgImage.getWidth) {
      JOptionPane.showMessageDialog(null, "Foreground Image Is Bigger In One or Both Dimensions" + "\nCannot proceed with overlay." + "\n\n Please use smaller Image for foreground")
      return bgImage
    }
    val g: Graphics2D = bgImage.createGraphics
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.drawImage(bgImage, 0, 0, null)
    val alpha: Float = 0.7f
    val composite: AlphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)
    g.setComposite(composite)
    g.drawImage(fgImage, 410, 260, null)
    g.dispose
    bgImage
  }

  private def parseManifest(inXml: InputStream) = {
    val xml = XML.load(Source.fromInputStream(inXml))
    val course = xml \\ "course"
    val lectures = (course \\ "lecture").map(lecture => {
      val path = (lecture \ "@path").text
      val slides = (lecture \\ "slide").map(slide => {
        val path = (slide \ "@path").text
        val sound = (slide \ "@sound").text
        Slide(path, sound)
      })
      Lecture(path, slides)
    })
    //val lArr = lectures.toArray
    //println(lArr)
    lectures
  }

  val lectures: List[Lecture] = {
    val manifestStream: InputStream = aesEncrypter.decryptContent("manifest.xml")
    val lectures = parseManifest(manifestStream)
    //lectures.foreach(l => println(l.slides.size))
    lectures.toList
  }

  private def loadLectureContent(lectureNumber: Int) = {
    //println("DEBUG: ContentManager content request for lecture " + lectureNumber)
    val lecture = lectures(lectureNumber)
    val previewPath = lecture.path + "/" + lecture.slides.head.path
    val imageInputStream: InputStream = aesEncrypter.decryptContent(previewPath)
    val buttonPlayBigImgURL = Thread.currentThread.getContextClassLoader.getResourceAsStream("resource/button_play_big.png")
    val contentWithPlay = overlayImages(ImageIO.read(imageInputStream), ImageIO.read(buttonPlayBigImgURL))
    contentWithPlay
  }

  def getLectureDescription(lectureNumber: Int): LectureDescription = {
    new LectureDescription("Lecture " + (lectureNumber + 1), loadLectureContent(lectureNumber))
  }

  private def getLecturesCount(): Int = {
    lectures.length
  }

  private def getSlidesCount(lectureNumber: Int): Int = {
    lectures(lectureNumber).slides.length
  }

  def getLecturesDescriptions: Seq[LectureDescription] = {
    val lds = for (lectureNum <- 0 until getLecturesCount) yield {
      getLectureDescription(lectureNum)
    }
    lds
  }

  private def getSlideContent(previewPath: String) = {
    val imageInputStream: InputStream = aesEncrypter.decryptContent(previewPath)
    val content = ImageIO.read(imageInputStream)
    content
  }

  private def getSlideSoundUrl(lecturePath: String, slideSoundPath: String) = {
    val tempDir = System.getProperty("java.io.tmpdir")
    val soundPath: String = tempDir + "/sound.wav"
    // TODO: Completely reconsider code with media
    val soundFile: String = lecturePath + "/" + slideSoundPath
    val BUFFER_SIZE: Int = 2048
    val buffer: Array[Byte] = new Array[Byte](BUFFER_SIZE)
    val dest = new FileOutputStream(soundPath)
    val soundBytes = aesEncrypter.decryptContent(soundFile)
    Stream.continually(soundBytes.read(buffer)).takeWhile(-1 !=).foreach(dest.write(buffer, 0, _))
    dest.flush
    dest.close
    val url: URL = new File(soundPath).toURL
    url
  }

  def getSlideInfo(lectureNum: Int, slideNumber: Int): SlideInfo = {
    //println("slide requested: " + slideNumber)

    if (slideNumber >= getSlidesCount(lectureNum)) null
    else {
      val lecture = lectures(lectureNum)
      val slide = lecture.slides(slideNumber)

      val title = "Slide " + (slideNumber + 1)
      val fullTitle = "Lecture " + (lectureNum + 1) + " - Slide " + (slideNumber + 1)
      val previewPath = lecture.path + "/" + slide.path

      new SlideInfo(slideNumber, title, fullTitle, getSlideContent(previewPath), getSlideSoundUrl(lecture.path, slide.sound))
    }
  }

  def getSlidesInfo(lectureNum: Int): Seq[SlideInfo] = {
    val slidesInfo = for (slideNum <- 0 until getSlidesCount(lectureNum)) yield {
      getSlideInfo(lectureNum, slideNum)
    }
    slidesInfo
  }
}
