package net.ivoah.books

import java.nio.file.*
import net.ivoah.vial.*
import com.typesafe.config.ConfigFactory

import java.time.LocalDate

class Endpoints {
  private val config = ConfigFactory.load()

  val router = Router {
    case ("GET", "/", _) => Response(
        Templates.root(Database.allBooks()),
        headers = Map("Content-Type" -> Seq("text/html; charset=UTF-8"))
      )
    case ("GET", s"/static/$file", _) => Response.forFile(Paths.get("static"), Paths.get(file))
    case ("GET", s"/$book_id", _) =>
      book_id.toIntOption.flatMap(Database.getBook).map { book =>
        Response(Templates.book(book))
      }.getOrElse(Response.NotFound())
    case ("POST", s"/$book_id/add_bookmark", request) =>
      if (!request.auth.contains((config.getString("auth.username"), config.getString("auth.password")))) {
        Response.Unauthorized()
      } else if (book_id.toIntOption.flatMap(Database.getBook).isEmpty) {
        Response.NotFound()
      } else {
        request.form.expect("location", "date") { (location: String, date: String) =>
          val newBookmark = Bookmark(book_id.toInt, location, LocalDate.parse(date))
          if (Database.addBookmark(newBookmark)) Response.Redirect(s"/$book_id")
          else Response.InternalServerError("Could not add bookmark")
        }
      }
    case ("POST", s"/$book_id/add_quote", request) =>
      if (!request.auth.contains((config.getString("auth.username"), config.getString("auth.password")))) {
        Response.Unauthorized()
      } else if (book_id.toIntOption.flatMap(Database.getBook).isEmpty) {
        Response.NotFound()
      } else {
        request.form.expect("quote", "location", "date") { (quote: String, location: String, date: String) =>  
          val newQuote = Quote(book_id.toInt, 0, quote, location, LocalDate.parse(date))
          if (Database.addQuote(newQuote)) Response.Redirect(s"/$book_id#lastQuote")
          else Response.InternalServerError("Could not add quote")
        }
      }
  }
}
