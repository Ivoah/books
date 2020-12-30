%import markdown
%from datetime import date

<!DOCTYPE html>
<html>
    <head>
        <title>{{book['title']}}</title>
        <link rel="icon" href="/static/favicon.png">
        <link rel="stylesheet" href="/static/style.css">
    </head>
    <body>
        <a href="/">All books</a>
        <h1>{{book['title']}} - {{book['author']}}</h1>
        <h3>{{book['started']}} – {{book['finished'] or 'Unfinished'}}</h3>
        %for quote in quotes:
            <hr>
            {{!markdown.markdown(quote['quote'])}}
            ---
            <p>
                {{quote['location']}}<br>
                {{quote['date']}}
            </p>
        %end
        <hr>
        <form action="/{{book['isbn']}}/add_quote" method="post">
            <p><textarea name="quote"></textarea></p>
            ---
            <p>
                <input type="text" name="location"><br>
                <input type="date" name="date" value="{{date.today().isoformat()}}">
            </p>
            <input type="submit" value="Add quote">
        </form>
    </body>
</html>
