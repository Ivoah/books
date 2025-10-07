package net.ivoah.books

import java.text.SimpleDateFormat
import scalatags.Text.all._

object Templates {
  private val isoFormat = new SimpleDateFormat("yyyy-MM-dd")

  def root(books: Seq[Book]): String = "<!DOCTYPE html>\n" + html(
    head(
      tag("title")("Noah's booklist"),
      link(rel:="icon", href:="/static/favicon.png"),
      link(rel:="stylesheet", href:="/static/style.css")
    ),
    body(
      h1("Noah's booklist"),
      table(
        thead(
          tr(th("ISBN"), th("Title"), th("Author"), th("Date started"), th("Date finished"), th("Quotes"))
        ),
        for (book <- books) yield {
          tr(id:=s"${book.book_id}",
            td(a(href:=s"https://openlibrary.org/isbn/${book.isbn}", book.isbn)),
            td(a(href:=s"/${book.book_id}", book.title, br(), span(`class`:="subtitle", book.subtitle))),
            td(book.author),
            td(book.started.toString),
            td(book.finished.getOrElse("Unfinished").toString),
            td(book.quotes.length)
          )
        }
      )
    )
  )

  def book(book: Book): String = "<!DOCTYPE html>\n" + html(
    head(
      tag("title")(s"${book.title}"),
      link(rel:="icon", href:="/static/favicon.png"),
      link(rel:="stylesheet", href:="/static/style.css")
    ),
    body(
      a(`class`:="lnav", href:="/", "< all books"),
      h1(s"${book.title} - ${book.author}"),
      h3(book.subtitle),
      h3(s"${book.started} - ${book.finished.getOrElse("Unfinished")}"),
      div(id:="bookmarks",
        book.bookmarks.map(b => frag(s"${b.date}: ${b.location}"))
          .appended(form(action:=s"/${book.book_id}/add_bookmark", method:="post", display:="inline",
            input(`type`:="date", name:="date", value:=isoFormat.format(new java.util.Date())), ": ",
            input(`type`:="text", name:="location"), " ",
            input(`type`:="submit", value:="Add bookmark")
          ))
          .zip(Seq.fill(book.bookmarks.length + 1)(frag(" | ")))
          .flatMap{(a, b) => Seq(a, b)}
          .dropRight(1)
      ),
      for ((quote, i) <- book.quotes.zipWithIndex) yield {
        div(if (i + 1 == book.quotes.length) id:="lastQuote" else frag(),
          hr(),
          raw(Markdown.render(quote.quote)),
          "---",
          p(`class`:="footer",
            quote.location,
            br(),
            quote.date.toString
          )
        )
      },
      hr(),
      form(action:=s"/${book.book_id}/add_quote", method:="post",
        p(textarea(name:="quote")),
        "---",
        p(
          input(`type`:="text", name:="location"), br(),
          input(`type`:="date", name:="date", value:=isoFormat.format(new java.util.Date()))
        ),
        input(`type`:="submit", value:="Add quote")
      )
    )
  )
}
