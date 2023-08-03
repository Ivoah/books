import os
import json
import pymysql
import operator
from bottle import *
from datetime import date

try:
    os.chdir(os.path.dirname(__file__))
except FileNotFoundError:
    pass

with open('credentials.json') as f:
    CREDS = json.load(f)

def db_connection():
    return pymysql.connect(
        host='books.ivoah.net',
        db='books',
        cursorclass=pymysql.cursors.DictCursor,
        **CREDS
    )

@get('/')
def main():
    db = db_connection()
    with db.cursor() as cursor:
        cursor.execute('''
            SELECT
                books.isbn,
                title,
                subtitle,
                author,
                started,
                finished,
                count(quotes.isbn) AS quotes
            FROM books LEFT JOIN quotes USING (isbn)
            GROUP BY books.isbn
            ORDER BY started
        ''')
        books = cursor.fetchall()
    db.close()
    return template('root.tpl', books=books)

@get('/<isbn>')
def book(isbn):
    db = db_connection()
    with db.cursor() as cursor:
        cursor.execute('SELECT * FROM books WHERE isbn=%s', isbn)
        book = cursor.fetchone()
        cursor.execute('SELECT * FROM quotes WHERE isbn=%s ORDER BY id', isbn)
        quotes = cursor.fetchall()
        cursor.execute('SELECT * FROM bookmarks WHERE isbn=%s ORDER BY date', isbn)
        bookmarks = cursor.fetchall()
    db.close()
    return template('book.tpl', book=book, quotes=quotes, bookmarks=bookmarks)

@post('/<isbn>/add_quote')
@auth_basic(lambda u, p: {'user': u, 'password': p} == CREDS)
def add_quote(isbn):
    quote = request.forms.quote
    location = request.forms.location
    date = request.forms.date

    print(quote)

    db = db_connection()
    with db.cursor() as cursor:
        cursor.execute('SELECT IFNULL(max(id), 0) FROM quotes WHERE isbn=%s', isbn)
        last_id, = map(int, cursor.fetchone().values())
        cursor.execute('''
            INSERT INTO quotes (isbn, id, quote, location, date)
            VALUES (%s, %s, %s, %s, %s);
        ''', (isbn, last_id + 1, quote, location, date))
        db.commit()
    db.close()
    redirect(f'/{isbn}#last')

@post('/<isbn>/add_bookmark')
@auth_basic(lambda u, p: {'user': u, 'password': p} == CREDS)
def add_bookmark(isbn):
    date = request.forms.date
    location = request.forms.location

    db = db_connection()
    with db.cursor() as cursor:
        cursor.execute('''
            INSERT INTO bookmarks (isbn, date, location)
            VALUES (%s, %s, %s);
        ''', (isbn, date, location))
        db.commit()
    db.close()
    redirect(f'/{isbn}')

application = default_app()

if __name__ == '__main__':
    get('/static/<filename>')(lambda filename: static_file(filename, root='static/'))
    run(app=application, host='0.0.0.0', port=8080, debug=True, reloader=True)
