package model

import java.security.MessageDigest
import java.util.Arrays
import java.io._
import javax.crypto.{Cipher, CipherOutputStream}
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}
import java.util.zip._

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

  private def getKeySpecForContent() = {
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

  private def zipContent(rootDir: File, aFile: File, zipOut: ZipOutputStream): Unit = {
    if (aFile.isFile) {
      println("[FILE] " + aFile.getName)
      print("Adding: " + aFile)
      val fi: FileInputStream = new FileInputStream(aFile.getPath)
      val origin: BufferedInputStream = new BufferedInputStream(fi, BUFFER_SIZE)
      val relativePath = rootDir.toURI.relativize(aFile.toURI).getPath
      val shaString: String = getSha1String(relativePath)
      val entry: ZipEntry = new ZipEntry(shaString)
      println(" [SHA1: " + shaString + "]")
      zipOut.putNextEntry(entry)

      // http://stackoverflow.com/questions/4905393/scala-inputstream-to-arraybyte
      val originEncrypted = new ByteArrayOutputStream
      encrypt(origin, originEncrypted, getKeySpecForContent)
      zipOut.write(originEncrypted.toByteArray, 0, originEncrypted.size)
      //Stream.continually(originEncrypted.read(buffer)).takeWhile(-1 !=).foreach(zipOut.write(buffer, 0, _))
      origin.close
    } else if (aFile.isDirectory) {
      println("[DIR] " + aFile.getName)
      val listOfFiles: Array[File] = aFile.listFiles
      if (listOfFiles != null) {
        listOfFiles.foreach(zipContent(rootDir, _, zipOut))
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
    val encryptedFilePath = new File(rootDir, encryptedFileName)
    val destinationFile = new FileOutputStream(encryptedFilePath)
    val checksum: CheckedOutputStream = new CheckedOutputStream(destinationFile, new Adler32)
    val zipOut: ZipOutputStream = new ZipOutputStream(new BufferedOutputStream(checksum))
    zipContent(dirToEncryptPath, dirToEncryptPath, zipOut)
    zipOut.close
  }

  def encryptContentKey() = {
    val ids = getIdsWindows()
    val key = getAesKeyFromIds(ids)
    encrypt(new FileInputStream(contentKeyName), new FileOutputStream(contentKeyName + "_enc"), key)
  }

  /*
  def decryptContent(in: InputStream, out: OutputStream) = {
    val key = getKeySpecForContent()
    //decrypt(new FileInputStream(encryptedFilePath), new FileOutputStream(unzippedFilename), key)
    decrypt(in, out, key)
  }
  */

  def decryptContent(in: InputStream, filePath: String) = {
    val shaString = getSha1String(filePath)
    val encryptedZipStream = new ZipInputStream(in)
    Stream.
      continually(encryptedZipStream.getNextEntry).
      find(_.getName == shaString) match {
      case Some(x: ZipEntry) =>
        val zipEntryStream = new ByteArrayOutputStream
        Stream.continually(encryptedZipStream.read(buffer)).takeWhile(-1 !=).foreach(zipEntryStream.write(buffer, 0, _))
        val encryptedIn = new ByteArrayInputStream(zipEntryStream.toByteArray, 0, zipEntryStream.size)
        val decryptedOut = new ByteArrayOutputStream
        decrypt(encryptedIn, decryptedOut, getKeySpecForContent)
        new ByteArrayInputStream(decryptedOut.toByteArray, 0, decryptedOut.size)
      case None => throw new FileNotFoundException("Element of content is not found.")
    }
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
    val encryptedFileName = "CourseContent.enc"
    aesEnc.encryptContent(rootDir, dirToEncrypt, encryptedFileName)
    //    encryptContentKey()
    //    decryptContent(encryptedFilePath)
  }
}
