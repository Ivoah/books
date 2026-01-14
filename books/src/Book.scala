package net.ivoah.books

import java.sql.Connection
import scala.collection.mutable
import scala.util.Using
import java.sql.ResultSet
import java.time.LocalDate

case class Book(book_id: Int,
                isbn: String,
                title: String,
                subtitle: String,
                author: String,
                started: LocalDate,
                finished: Option[LocalDate])(using Reconnector) {
  lazy val quotes: Seq[Quote] = Database.getQuotes(book_id)
  lazy val bookmarks: Seq[Bookmark] = Database.getBookmarks(book_id)
}

object Book {
  def apply(r: ResultSet)(using Reconnector): Book = Book(
    r.getInt("book_id"),
    r.getString("isbn"),
    r.getString("title"),
    Option(r.getString("subtitle")).getOrElse(""),
    r.getString("author"),
    r.getDate("started").toLocalDate,
    Option(r.getDate("finished")).map(_.toLocalDate)
  )
}

case class Quote(book_id: Int,
                 quote_id: Int,
                 quote: String,
                 location: String,
                 date: LocalDate)

object Quote {
  def apply(r: ResultSet): Quote = Quote(
    r.getInt("book_id"),
    r.getInt("quote_id"),
    r.getString("quote"),
    r.getString("location"),
    r.getDate("date").toLocalDate
  )
}

case class Bookmark(book_id: Int,
                    location: String,
                    date: LocalDate)

object Bookmark {
  def apply(r: ResultSet): Bookmark = Bookmark(
    r.getInt("book_id"),
    r.getString("location"),
    r.getDate("date").toLocalDate
  )
}
