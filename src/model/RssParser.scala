package model

import scala.xml._
import java.net.URL

case class RssChannel(name: String, items: Seq[RssItem])

case class RssItem(title: String, description: String, link: String)

// TODO: process exceptions

object RssReader {
  def main(args: Array[String]): Unit = {
    val rssChannels = extractRss("""http://www.scala-lang.org/featured/rss.xml""")
    printChannels(rssChannels)
    feedsToHtml(rssChannels)
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

  def feedsToHtml(channels: Seq[RssChannel]) = {
    val html =
      <body>
        {for (channel <- channels) yield
          <h2>
            {channel.name}
          </h2>
            <ul>
              {for (item <- channel.items) yield <li>
              {item.title} <a href={ item.link }> { ">>" } </a>
            </li>}
            </ul>}
      </body>

    XML.saveFull("rss.html", html, "UTF-8", true, null)
  }
}