# akka-http-etag-example

This is a example project that shows how to effectively implement ETags and `Last-Modified` headers in akka-http projects.

Start application by running `sbt run`.

It will start HTTP server on port `8080`, there are 2 endpoints you can try `/books` and `/books-etags`, both accept single parameter - the book id.

To read more about this project, see my blog post: https://www.wlangiewicz.com/2016/11/25/etags-in-akka-http/

I have explained everything in the comments in `BooksApi`, this is an overview of how such requests are processed:

There are 2 main cases to consider:

## Request without ETag
We need to fetch full resource from the database, and return it to the client together with ETag header, hoping that client will send it next time it makes request to our service
## Request with ETag 
In this case we first need to actually perform ETag validation to see if ETag provided by the client is valid or not.
##### Request with invalid ETag
In this case, client could have received resource long time ago, and the resource has changed since that time, making it's cached version invalid.
We need to return updated resource together with ETag header (the same case as if request was set without ETag)
##### Request with valid ETag
If after ETag validation we discover that client has up-to-date resource, we can only return 304 HTTP status code (without body) to indicate that the resource hasn't change on the server side.

