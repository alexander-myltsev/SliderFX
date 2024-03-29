package model

import java.net.URL
import xml.{Node, XML}
import controller.{RssItem, RssChannel}
import java.security.Security
import java.io.InputStream

class InformationProvider {
  def getNews(): Option[Seq[RssChannel]] = {
    try {
      extractRss("""http://news.developer.nvidia.com/rss.xml""")
    } catch {
      case ex => None
    }
  }

  def extractRss(urlStr: String): Option[Seq[RssChannel]] = {
    val url = new URL(urlStr)
    val conn = url.openConnection
    val xml = XML.load(conn.getInputStream)

    val rssChannels = (xml \\ "channel").map(channel => {
      val channelTitle = (channel \ "title").text
      val rssItems = extractRssChannel(channel)
      RssChannel(channelTitle, rssItems)
    })
    Some(rssChannels)
  }

  def printChannels(channels: Seq[RssChannel]): Unit = {
    channels.foreach(channel => {
      println("*** " + channel.name + " ***")
      channel.items.foreach(item => {
        println("\titem: " + item.title)
        println("\tdescr: " + item.description)
        println("\tlink: " + item.link)
        println("--------------------")
      })
    })
  }

  def extractRssChannel(rssChannel: Node): Seq[RssItem] = {
    (rssChannel \\ "item").map(item => {
      val title = (item \ "title").text
      val description = (item \ "description").text
      val link = (item \ "link").text
      RssItem(title, description, link)
    })
  }

  def channelsToHtml(channels: Option[Seq[RssChannel]]) = {
    channels match {
      case None => "No news available because there is no Internet connection"
      case Some(channels) => 
	val html =
	  <body>
	    {for (channel <- channels) yield
              <h2>
               {channel.name}
              </h2>
              <items>
               {for (item <- channel.items) yield
	        <p>
               {item.title}<a href={item.link}>
               {">>"}
               </a>
              </p>}
             </items>}
          </body>

        //XML.saveFull("rss.html", html, "UTF-8", true, null)
        html.toString
    }
  }

  def sendQuestion(subject: String, question: String, filename: String, fileStream: InputStream): Unit = {
    val sendTo: Array[String] = Array("alexander.myltsev@gmail.com")
    //val emailMsgTxt: String = question
    //val emailSubjectTxt: String = "A test from edu-cuda"
    val emailFromAddress: String = "alexander.myltsev@gmail.com"
    Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider)
    SmtpMailer.sendSSLMessage(sendTo, subject, question, filename, fileStream, emailFromAddress)
    System.out.println("Sucessfully Sent mail to: " + sendTo.mkString(", "))
  }
}
