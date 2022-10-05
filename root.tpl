<!DOCTYPE html>
<html>
    <head>
        <title>Noah's booklist</title>
        <link rel="icon" href="/static/favicon.png">
        <link rel="stylesheet" href="/static/style.css">
    </head>
    <body>
        <h1>Noah's booklist</h1>
        <table>
            <thead>
                <tr>
                    <th>ISBN</th>
                    <th>Title</th>
                    <th>Author</th>
                    <th>Date started</th>
                    <th>Date finished</th>
                    <th>Quotes</th>
                </tr>
            </thead>
            %for row in books:
                <tr id="{{row['isbn']}}">
                    <td><a href="https://openlibrary.org/isbn/{{row['isbn']}}">{{row['isbn']}}</a></td>
                    <td>
                        <a href="/{{row['isbn']}}">
                            {{row['title']}}<br>
                            <span class="subtitle">{{row['subtitle']}}</span>
                        </a>
                    </td>
                    <td>{{row['author']}}</td>
                    <td>{{row['started']}}</td>
                    <td>{{row['finished'] or 'Unfinished'}}</td>
                    <td>{{row['quotes']}}</td>
                </tr>
            %end
        </table>
    </body>
</html>
