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
        <a class="lnav" href="/">&lt; all books</a>
        <h1>{{book['title']}} - {{book['author']}}</h1>
        <h3>{{book['subtitle']}}</h3>
        <h3>{{book['started']}} – {{book['finished'] or 'Unfinished'}}</h3>
        <div>
            {{!' | '.join('<span class="bookmark">{date}: {location}</span>'.format(**bookmark) for bookmark in bookmarks)}}
            %if not book['finished']:
                %if bookmarks:
                    |
                %end
                <form action="/{{book['isbn']}}/add_bookmark" method="post" style="display: inline">
                    <input type="date" name="date" value="{{date.today().isoformat()}}">:
                    <input type="text" name="location">
                    <input type="submit" value="Add bookmark">
                </form>
            %end
        </div>
        %for i, quote in enumerate(quotes):
            <div{{!' id="last"' if (i == len(quotes) - 1) else ''}}>
                <hr>
                <div class="markdown">
                    {{!markdown.markdown(quote['quote'])}}
                </div>
                ---
                <p class="footer">
                    {{quote['location']}}<br>
                    {{quote['date']}}
                </p>
            </div>
        %end
        <hr>
        %if not book['finished']:
            <form action="/{{book['isbn']}}/add_quote" method="post">
                <p><textarea name="quote"></textarea></p>
                ---
                <p>
                    <input type="text" name="location"><br>
                    <input type="date" name="date" value="{{date.today().isoformat()}}">
                </p>
                <input type="submit" value="Add quote">
            </form>
        %end
    </body>
</html>
