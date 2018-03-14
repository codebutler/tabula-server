# tabula-server

A very simple HTTP server API for [Tabula](https://github.com/tabulapdf/tabula-java/),
a library for extracting tables from PDF files.

## API

### GET /scrape?url=the_pdf_url

Assuming a PDF containing a table like this:

| Id | Name  | Email
|----|-------|-------------------|
| 1  | Eric  | eric@example.com  |
| 2  | Logan | logan@example.com |
| 3  | Emily | emily@example.com |

Returns JSON:

```json
{
  "pages": [
    {
      "tables": [
        {
          "data": [
            { "Id": "1", "Name": "Eric", "Email": "eric@example.com" },
            { "Id": "2", "Name": "Logan", "Email": "logan@example.com" },
            { "Id": "3", "Name": "Emaily", "Email": "emily@example.com" },
          ]
        }
      ]
    }
  ]
}
```

## Build/Run

```
$ ./gradlew build
$ ./gradlew run
```

## Deploy

Set the `JWT_SECRET` in your production environment. For example:

```
$ dokku config:set app_name JWT_SECRET=put_secret_here
```