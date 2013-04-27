package model

import java.net.URL
import xml.{Node, XML}
import controller.{RssItem, RssChannel}

object InformationProvider {
  def getNews(): Seq[RssChannel] = {
    extractRss("""http://www.scala-lang.org/featured/rss.xml""")
  }

  def extractRss(urlStr: String): Seq[RssChannel] = {
    val url = new URL(urlStr)
    val conn = url.openConnection
    val xml = XML.load(conn.getInputStream)

    (xml \\ "channel").map(channel => {
      val channelTitle = (channel \ "title").text
      val rssItems = extractRssChannel(channel)
      RssChannel(channelTitle, rssItems)
    })
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

  def channelsToHtml(channels: Seq[RssChannel]) = {
    val html =
      <body>
        {for (channel <- channels) yield
        <h2>
          {channel.name}
        </h2>
          <items>
            {for (item <- channel.items) yield
            <p>
              {item.title}<a href={item.link}>  {">>"} </a>
          </p>}
          </items>}
      </body>

    //XML.saveFull("rss.html", html, "UTF-8", true, null)
    html.toString
  }

  def sentQuestion(question: String): Unit = {
    println("INFO: InformationProvider. Question sent: " + question)
  }
}