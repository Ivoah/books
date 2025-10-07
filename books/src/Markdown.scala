package net.ivoah.books

import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

import scala.jdk.CollectionConverters.*
import scala.util.matching.Regex

object Markdown {
  private val extensions = Seq(StrikethroughExtension.create()).asJava
  private val parser = Parser.builder().extensions(extensions).build()
  private val htmlRenderer = HtmlRenderer.builder().extensions(extensions).build()
  
  def render(markdown: String): String = htmlRenderer.render(parser.parse(markdown))
}
