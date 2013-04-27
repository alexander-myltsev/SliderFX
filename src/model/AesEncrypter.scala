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
    val sha = MessageDigest.getInstance("SHA-1")
    val digest = sha.digest(magicKey)
    val key = Arrays.copyOf(digest, 16) // use only first 128 bit
    val secretKeySpec = new SecretKeySpec(key, "AES")
    secretKeySpec
  }

  def getKeySpecForContent() = {
    /*
    val f = new File(contentKeyName)
    if (f.exists) {
      val bytes = new Array[Byte](16)
      val spec = new SecretKeySpec(bytes, "AES")
      new FileInputStream(f).read(bytes)
      spec
    }
    else {
      val kgen = KeyGenerator.getInstance("AES")
      kgen.init(128)
      val key = kgen.generateKey
      val bytes = key.getEncoded
      val outputStream = new FileOutputStream(f)
      outputStream.write(bytes)
      outputStream.close
      val spec = new SecretKeySpec(bytes, "AES")
      spec
    }
    */
    //val f = ClassLoader.getSystemResourceAsStream("resource/aes_key")
    val f = Thread.currentThread.getContextClassLoader.getResourceAsStream("resource/aes_key")
    val bytes = new Array[Byte](16)
    val spec = new SecretKeySpec(bytes, "AES")
    f.read(bytes)
    spec
  }

  val BUFFER_SIZE: Int = 2048
  val buffer: Array[Byte] = new Array[Byte](BUFFER_SIZE)

  def zipContent(rootDir: File, aFile: File, zipOut: ZipOutputStream): Unit = {
    if (aFile.isFile) {
      System.out.println("[FILE] " + aFile.getName)
      System.out.println("Adding: " + aFile)
      val fi: FileInputStream = new FileInputStream(aFile.getPath)
      val origin: BufferedInputStream = new BufferedInputStream(fi, BUFFER_SIZE)
      val relative = rootDir.toURI.relativize(aFile.toURI).getPath
      val entry: ZipEntry = new ZipEntry(relative)
      zipOut.putNextEntry(entry)

      // TODO: Consider http://stackoverflow.com/questions/4905393/scala-inputstream-to-arraybyte
      def readFile(): Unit = {
        val count = origin.read(buffer, 0, BUFFER_SIZE)
        if (count != -1) {
          zipOut.write(buffer, 0, count)
          readFile()
        }
      }
      readFile()
      origin.close
    } else if (aFile.isDirectory) {
      System.out.println("[DIR] " + aFile.getName)
      val listOfFiles: Array[File] = aFile.listFiles
      if (listOfFiles != null) {
        listOfFiles.foreach(f => zipContent(rootDir, f, zipOut))
      } else {
        System.out.println(" [ACCESS DENIED]")
      }
    }
  }

  def encrypt(in: InputStream, out: OutputStream, key: SecretKeySpec) = {
    val iv = Array[Byte](0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f)
    val paramSpec = new IvParameterSpec(iv)
    val ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec)
    // Read in the cleartext bytes and write to out to encrypt
    val encryptedOut = new CipherOutputStream(out, ecipher)
    def readFile(): Unit = {
      val numRead = in.read(buffer)
      if (numRead != -1) {
        encryptedOut.write(buffer, 0, numRead)
        readFile
      }
    }
    readFile()
    encryptedOut.close
  }

  def decrypt(in: InputStream, out: OutputStream, key: SecretKeySpec) = {
    val iv = Array[Byte](0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f)
    val paramSpec = new IvParameterSpec(iv)
    val dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec)
    // Read in the decrypted bytes and write the cleartext to out
    val decryptedOut = new CipherOutputStream(out, dcipher)
    def readFile(): Unit = {
      val numRead = in.read(buffer)
      if (numRead != -1) {
        decryptedOut.write(buffer, 0, numRead)
        readFile
      }
    }
    readFile()
    decryptedOut.close
  }


  def encryptContent(rootDir: File, encryptedFilePath: String) = {
    val zipContentPath: String = "e:/Projects/ParallelCompute/CourseGUI/CourseContent.zip"
    val destinationFile: FileOutputStream = new FileOutputStream(zipContentPath)
    val destinationFileEncripted: FileOutputStream = new FileOutputStream(encryptedFilePath)
    val checksum: CheckedOutputStream = new CheckedOutputStream(destinationFile, new Adler32)
    val zipOut: ZipOutputStream = new ZipOutputStream(new BufferedOutputStream(checksum))
    zipContent(rootDir, rootDir, zipOut)
    zipOut.close

    println("Encripting...")
    val key = getKeySpecForContent()
    encrypt(new FileInputStream(zipContentPath), destinationFileEncripted, key)
    println("Encripting done")
  }

  def encryptContentKey() = {
    val ids = getIdsWindows()
    val key = getAesKeyFromIds(ids)
    encrypt(new FileInputStream(contentKeyName), new FileOutputStream(contentKeyName + "_enc"), key)
  }

  def decryptContent(in: InputStream, out:OutputStream) = {
    val key = getKeySpecForContent()
    //decrypt(new FileInputStream(encryptedFilePath), new FileOutputStream(unzippedFilename), key)
    decrypt(in, out, key)

    /*
    val zipFile = new ZipFile(unzippedFilename)
    val entry = zipFile.getEntry("Lecture1/Slide1.PNG")
    val inputStream: InputStream = zipFile.getInputStream(entry)

    val checksum: CheckedInputStream = new CheckedInputStream(new FileInputStream(unzippedFilename), new Adler32)
    val zis: ZipInputStream = new ZipInputStream(new BufferedInputStream(checksum))

    def readEntries(): Unit = {
      val entry = zis.getNextEntry
      if (entry != null) {
        val filePath: String = "CC/" + entry.getName
        val fl = new File(filePath)
        fl.getParentFile.mkdirs
        fl.createNewFile
        val fos: FileOutputStream = new FileOutputStream(filePath)
        val dest = new BufferedOutputStream(fos, BUFFER_SIZE)

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
        readEntries()
      }
    }
    readEntries()
    */
  }

  def main(args: Array[String]):Unit = {
    val encryptedFilePath: String = "e:/Projects/ParallelCompute/CourseGUI/CourseContent.enc"
    val fileToDecrypt = new File("e:/Projects/ParallelCompute/CourseGUI/CourseContent/")
    encryptContent(fileToDecrypt,encryptedFilePath)
    //encryptContentKey()
    //decryptContent(encryptedFilePath)
  }
}
