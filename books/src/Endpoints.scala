package net.ivoah.books

import java.sql.{Connection, DriverManager}
import java.nio.file.*
import net.ivoah.vial.*
import com.typesafe.config.ConfigFactory

import scala.util.Using

class Endpoints {
  private val config = ConfigFactory.load()
  implicit val db: Connection = DriverManager.getConnection(
    config.getString("db.connection"),
    config.getString("db.username"),
    config.getString("db.password")
  )

  val router = Router {
    case ("GET", "/", _) => Response(
        Templates.root(Book.getAll),
        headers = Map("Content-Type" -> Seq("text/html; charset=UTF-8"))
      )
    case ("GET", s"/static/$file", _) => Response.forFile(Paths.get(s"static/$file"))
    case ("GET", s"/$book_id", _) =>
      book_id.toIntOption.flatMap(Book.get).map { book =>
        Response(
          Templates.book(book),
          headers = Map("Content-Type" -> Seq("text/html; charset=UTF-8"))
        )
      }.getOrElse(Response.NotFound())
    case ("POST", s"/$book_id/add_bookmark", request) =>
      if (!request.auth.contains((config.getString("username"), config.getString("password")))) {
        Response.Unauthorized()
      } else if (book_id.toIntOption.flatMap(Book.get).isEmpty) {
        Response.NotFound()
      } else {
        Using.resource(db.prepareStatement("INSERT INTO bookmark (book_id, location, date) VALUES (?, ?, ?)")) { stmt =>
          stmt.setInt(1, book_id.toInt)
          stmt.setString(2, request.form("location").asInstanceOf[String])
          stmt.setDate(3, java.sql.Date.valueOf(request.form("date").asInstanceOf[String]))
          stmt.execute()
        }
        Response.Redirect(s"/$book_id")
      }
    case ("POST", s"/$book_id/add_quote", request) =>
      if (!request.auth.contains((config.getString("username"), config.getString("password")))) {
        Response.Unauthorized()
      } else if (book_id.toIntOption.flatMap(Book.get).isEmpty) {
        Response.NotFound()
      } else {
        Using.resource(db.prepareStatement("INSERT INTO quote (book_id, quote_id, quote, location, date) VALUES (?, (SELECT COALESCE(max(quote_id), 0) + 1 FROM quote WHERE book_id = ?), ?, ?, ?)")) { stmt =>
          stmt.setInt(1, book_id.toInt)
          stmt.setInt(2, book_id.toInt)
          stmt.setString(3, request.form("quote").asInstanceOf[String])
          stmt.setString(4, request.form("location").asInstanceOf[String])
          stmt.setDate(5, java.sql.Date.valueOf(request.form("date").asInstanceOf[String]))
          stmt.execute()
        }
        Response.Redirect(s"/$book_id#lastQuote")
      }
  }
}
