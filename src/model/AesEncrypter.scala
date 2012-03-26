package model

import java.security.MessageDigest
import java.util.Arrays
import java.io._
import javax.crypto.{Cipher, CipherOutputStream}
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

// TODO: replace all def readFile

case class Ids(motherboardID: String, processorID: String) {
  override def toString = "Ids[" + motherboardID + "][" + processorID + "]"
}

class AesEncrypter {
  val contentKeyName: String = "aes_key"
  val shaInstance = MessageDigest.getInstance("SHA-1")
  val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

  def getIdsWindows() = {
    def get(com: Array[String]) = {
      var process = Runtime.getRuntime.exec(com)
      process.getOutputStream.close
      var reader = new BufferedReader(new InputStreamReader(process.getInputStream))
      val resp = {
        reader.readLine
        reader.readLine
        reader.readLine
      }
      resp
    }

    Ids(
      get(Array("CMD", "/C", "WMIC BASEBOARD GET SerialNumber")),
      get(Array("CMD", "/C", "WMIC CPU GET ProcessorId")))
  }

  def getAesKeyFromIds(ids: Ids) = {
    val magicKey = (ids.motherboardID + ids.processorID).getBytes("UTF-8")
    val digest = shaInstance.digest(magicKey)
    val key = Arrays.copyOf(digest, 16) // use only first 128 bit
    val secretKeySpec = new SecretKeySpec(key, "AES")
    secretKeySpec
  }

  private val keySpecForContent = {
    val f = Thread.currentThread.getContextClassLoader.getResourceAsStream("resource/aes_key")
    val bytes = new Array[Byte](16)
    val spec = new SecretKeySpec(bytes, "AES")
    f.read(bytes)
    spec
  }

  private def getSha1String(msg: String): String = {
    val msgBytes = msg.getBytes("UTF-8")
    val digest = shaInstance.digest(msgBytes)
    digest.map("%02X" format _).mkString
  }

  val BUFFER_SIZE: Int = 4096
  val buffer: Array[Byte] = new Array[Byte](BUFFER_SIZE)

  private def encryptContent(rootDir: File, aFile: File, encryptedDirPath: File): Unit = {
    if (aFile.isFile) {
      println("[FILE] " + aFile.getName)
      print("Adding: " + aFile)
      val fi: FileInputStream = new FileInputStream(aFile)
      val origin: BufferedInputStream = new BufferedInputStream(fi, BUFFER_SIZE)

      val relativePath = rootDir.toURI.relativize(aFile.toURI).getPath
      val shaString: String = getSha1String(relativePath)

      println(" [SHA1: " + shaString + "]")

      val encryptedFilePath = new File(encryptedDirPath, shaString)
      val encryptedFile = new FileOutputStream(encryptedFilePath)

      // http://stackoverflow.com/questions/4905393/scala-inputstream-to-arraybyte
      val originEncrypted = new ByteArrayOutputStream
      encrypt(origin, originEncrypted, keySpecForContent)

      encryptedFile.write(originEncrypted.toByteArray)

      encryptedFile.flush
      encryptedFile.close
      origin.close
    } else if (aFile.isDirectory) {
      println("[DIR] " + aFile.getName)
      val listOfFiles: Array[File] = aFile.listFiles
      if (listOfFiles != null) {
        listOfFiles.foreach(encryptContent(rootDir, _, encryptedDirPath))
      } else {
        System.out.println(" [ACCESS DENIED]")
      }
    }
  }


  private def encrypt(in: InputStream, out: OutputStream, key: SecretKeySpec) = {
    val iv = Array[Byte](0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f)
    val paramSpec = new IvParameterSpec(iv)
    cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec)
    // Read in the cleartext bytes and write to out to encrypt
    val encryptedOut = new CipherOutputStream(out, cipher)
    Stream.continually(in.read(buffer)).takeWhile(-1 !=).foreach(encryptedOut.write(buffer, 0, _))
    encryptedOut.close
  }

  private def decrypt(in: InputStream, out: OutputStream, key: SecretKeySpec) = {
    val iv = Array[Byte](0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f)
    val paramSpec = new IvParameterSpec(iv)
    cipher.init(Cipher.DECRYPT_MODE, key, paramSpec)
    // Read in the decrypted bytes and write the cleartext to out
    val decryptedOut = new CipherOutputStream(out, cipher)
    Stream.continually(in.read(buffer)).takeWhile(-1 !=).foreach(decryptedOut.write(buffer, 0, _))
    decryptedOut.close
  }

  def encryptContent(rootDir: File, dirToEncrypt: String, encryptedFileName: String): Unit = {
    val dirToEncryptPath = new File(rootDir, dirToEncrypt)
    val encryptedDirPath = new File(rootDir, encryptedFileName)
    encryptedDirPath.mkdir
    encryptContent(dirToEncryptPath, dirToEncryptPath, encryptedDirPath)
  }

  def encryptContentKey() = {
    val ids = getIdsWindows()
    val key = getAesKeyFromIds(ids)
    encrypt(new FileInputStream(contentKeyName), new FileOutputStream(contentKeyName + "_enc"), key)
  }

  def decryptContent(filePath: String) = {
    val shaString = getSha1String(filePath)
    val encryptedInFileOfContent = Thread.currentThread.getContextClassLoader.getResourceAsStream("resource/CourseContent_enc/" + shaString)
    val decryptedOut = new ByteArrayOutputStream
    decrypt(encryptedInFileOfContent, decryptedOut, keySpecForContent)
    new ByteArrayInputStream(decryptedOut.toByteArray)
  }
}

object T {
  def valueOf(buf: Array[Byte]): String = buf.map("%02X" format _).mkString

  def test1(args: Array[String]): Unit = {
    val sha = MessageDigest.getInstance("SHA-1")

    def printSha(msg: String) = {
      val digest: Array[Byte] = sha.digest(msg.getBytes("UTF-8"))
      val out = valueOf(digest)
      println(msg + ": " + out)
    }

    printSha("manifest")
    printSha("Lecture1/slide1.png")
    printSha("Lecture1/slide2.png")
    printSha("Lecture1/slide1.png")
    printSha("manifest")
    printSha("Lecture1/slide1.png")
  }

  def main(args: Array[String]): Unit = {
    val aesEnc = new AesEncrypter
    val rootDir = new File("d:/Projects/ParallelCompute/CourseGUI/")
    val dirToEncrypt = "CourseContent"
    val encryptedFileName = "CourseContent_enc"
    aesEnc.encryptContent(rootDir, dirToEncrypt, encryptedFileName)
    //    encryptContentKey()
    //    decryptContent(encryptedFilePath)
  }
}
