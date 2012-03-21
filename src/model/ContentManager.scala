package model.lectures

import model._
import controller.{SlideInfo, LectureDescription}
import javax.imageio.ImageIO
import scala.xml._
import scala.xml._
import java.io.{InputStream, FileOutputStream, File}
import java.util.zip.ZipFile
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

object ContentManager {
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

  def parseManifest(inXml: InputStream) = {
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

  val tempDir = System.getProperty("java.io.tmpdir")
  val decryptedFilename: String = tempDir + "/tmp"

  val lectures = decryptLectures()

  def decryptLectures() = {
    //val encryptedStream = ClassLoader.getSystemResourceAsStream("resource/CourseContent.enc")
    val encryptedStream = Thread.currentThread.getContextClassLoader.getResourceAsStream("resource/CourseContent")
    AesEncrypter.decryptContent(encryptedStream, new FileOutputStream(decryptedFilename))

    val zipFile = new ZipFile(decryptedFilename)
    val entry = zipFile.getEntry("manifest.xml")
    val manifestStream: InputStream = zipFile.getInputStream(entry)
    val lectures = parseManifest(manifestStream)
    //lectures.foreach(l => println(l.slides.size))
    lectures
  }

  def getLectureDescription(lectureNumber: Int): LectureDescription = {
    //val previewPath = "resource/Lectures/Lecture" + lectureNumber + "/Slide1.PNG"
    //val content = ImageIO.read(new File(previewPath))
    //val buttonPlayBigImgURL = ClassLoader.getSystemResource("resource/button_play_big.png")
    //val contentWithPlay = overlayImages(content, ImageIO.read(buttonPlayBigImgURL))
    //new LectureDescription(lectureNumber, "Lecture " + lectureNumber, contentWithPlay)

    val lect = lectures(lectureNumber)
    val previewPath = lect.path + "/" + lect.slides.head.path
    val zipFile = new ZipFile(decryptedFilename)
    val imageImputStream: InputStream = zipFile.getInputStream(zipFile.getEntry(previewPath))
    //val buttonPlayBigImgURL = ClassLoader.getSystemResource("resource/button_play_big.png")
    val buttonPlayBigImgURL = Thread.currentThread.getContextClassLoader.getResourceAsStream("resource/button_play_big.png")
    val contentWithPlay = overlayImages(ImageIO.read(imageImputStream), ImageIO.read(buttonPlayBigImgURL))
    new LectureDescription(lectureNumber, "Lecture " + (lectureNumber + 1), contentWithPlay)
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

  def getSlideInfo(lectureNum: Int, slideNumber: Int): SlideInfo = {
    //val previewPath = "resource/Lectures/Lecture" + lectureNum + "/Slide" + slideNumber + ".PNG"
    //val title = "Slide " + slideNumber
    //val content = ImageIO.read(new File(previewPath))
    //new SlideInfo(slideNumber, title, previewPath, content)

    if (slideNumber >= getSlidesCount(lectureNum)) null
    else {
      val lect = lectures(lectureNum)
      val sld = lect.slides(slideNumber)
      val previewPath = lect.path + "/" + sld.path
      val zipFile = new ZipFile(decryptedFilename)
      val imageImputStream: InputStream = zipFile.getInputStream(zipFile.getEntry(previewPath))
      val content = ImageIO.read(imageImputStream)
      val title = "Slide " + (slideNumber + 1)
      val fullTitle = "Lecture " + (lectureNum + 1) + " - Slide " + (slideNumber + 1)

      // TODO: Completely reconsider code with media
      val BUFFER_SIZE: Int = 2048
      val buffer: Array[Byte] = new Array[Byte](BUFFER_SIZE)
      val soundPath: String = tempDir + "/sound.wav"
      val dest = new FileOutputStream(soundPath)
      val soundZipEntry: String = lect.path + "/" + sld.sound
      val zis = zipFile.getInputStream(zipFile.getEntry(soundZipEntry))
      def readFile(): Unit = {
        val numRead = zis.read(buffer)
        if (numRead != -1) {
          dest.write(buffer, 0, numRead)
          readFile
        }
      }
      readFile()
      dest.flush
      dest.close

      val url: URL = new File(soundPath).toURL
      new SlideInfo(slideNumber, title, fullTitle, previewPath, content, url)
    }
  }

  def getSlidesInfo(lectureNum: Int): Seq[SlideInfo] = {
    val slds = for (slideNum <- 0 until getSlidesCount(lectureNum)) yield {
      getSlideInfo(lectureNum, slideNum)
    }
    slds
  }
}
