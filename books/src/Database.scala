package net.ivoah.books

import com.typesafe.config.ConfigFactory

object Database {
  private val config = ConfigFactory.load()
  given Reconnector(config.getString("db.url"))

  def allBooks(): Seq[Book] = sql"SELECT * FROM book ORDER BY started DESC".query(Book.apply)
  def getBook(id: Int): Option[Book] = sql"SELECT * FROM book where book_id=$id".query(Book.apply).headOption
  def getQuotes(id: Int): Seq[Quote] = sql"SELECT * FROM quote where book_id=$id ORDER BY quote_id".query(Quote.apply)
  def getBookmarks(id: Int): Seq[Bookmark] = sql"SELECT * FROM bookmark where book_id=$id ORDER BY date".query(Bookmark.apply)

  def addQuote(quote: Quote): Boolean = sql"""
    INSERT INTO quote (book_id, quote_id, quote, location, date)
    VALUES (${quote.book_id}, (SELECT COALESCE(max(quote_id), 0) + 1 FROM quote WHERE book_id = ${quote.book_id}), ${quote.quote}, ${quote.location}, ${quote.date})
  """.update() == 1

  def addBookmark(bookmark: Bookmark): Boolean = sql"""
    INSERT INTO bookmark (book_id, location, date)
    VALUES (${bookmark.book_id}, ${bookmark.location}, ${bookmark.date})
  """.update() == 1
}
