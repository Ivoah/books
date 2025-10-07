package net.ivoah.books

import java.sql.Connection
import scala.collection.mutable
import scala.util.Using

case class Book(book_id: Int,
                isbn: String,
                title: String,
                subtitle: String,
                author: String,
                started: java.util.Date,
                finished: Option[java.util.Date])(implicit db: Connection) {
  lazy val quotes: Seq[Quote] = Quote.forBook(book_id)
  lazy val bookmarks: Seq[Bookmark] = Bookmark.forBook(book_id)
}

object Book {
  def getAll(implicit db: Connection): Seq[Book] = Using.resource(db.createStatement()) { stmt =>
    val results = stmt.executeQuery("SELECT * FROM book ORDER BY started DESC")
    val buffer = mutable.Buffer[Book]()
    while (results.next()) {
      val book_id = results.getInt("book_id")
      buffer.append(Book(
        book_id,
        results.getString("isbn"),
        results.getString("title"),
        Option(results.getString("subtitle")).getOrElse(""),
        results.getString("author"),
        results.getDate("started"),
        Option(results.getDate("finished"))
      ))
    }
    buffer.toSeq
  }

  def get(book_id: Int)(implicit db: Connection): Option[Book] = Using(db.prepareStatement("SELECT * FROM book WHERE book_id = ?")) { stmt =>
    stmt.setInt(1, book_id)
    val results = stmt.executeQuery()
    results.next()
    Book(
      book_id,
      results.getString("isbn"),
      results.getString("title"),
      Option(results.getString("subtitle")).getOrElse(""),
      results.getString("author"),
      results.getDate("started"),
      Option(results.getDate("finished"))
    )
  }.toOption
}

case class Quote(book_id: Int,
                 quote_id: Int,
                 quote: String,
                 location: String,
                 date: java.util.Date)

object Quote {
  def forBook(book_id: Int)(implicit db: Connection): Seq[Quote] = Using.resource(db.prepareStatement("SELECT * FROM quote WHERE book_id = ? ORDER BY quote_id")) { stmt =>
    stmt.setInt(1, book_id)
    val results = stmt.executeQuery()
    val buffer = mutable.Buffer[Quote]()
    while (results.next()) {
      buffer.append(Quote(
        book_id,
        results.getInt("quote_id"),
        results.getString("quote"),
        results.getString("location"),
        results.getDate("date")
      ))
    }
    buffer.toSeq
  }
}

case class Bookmark(book_id: Int,
                    location: String,
                    date: java.util.Date)

object Bookmark {
  def forBook(book_id: Int)(implicit db: Connection): Seq[Bookmark] = Using.resource(db.prepareStatement("SELECT * FROM bookmark WHERE book_id = ? ORDER BY date")) { stmt =>
    stmt.setInt(1, book_id)
    val results = stmt.executeQuery()
    val buffer = mutable.Buffer[Bookmark]()
    while (results.next()) {
      buffer.append(Bookmark(
        book_id,
        results.getString("location"),
        results.getDate("date")
      ))
    }
    buffer.toSeq
  }
}
