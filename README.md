Kotlin DGS Framework example
=====

This repository is an example application for the [DGS Framework](https://netflix.github.io/dgs).
The example is a standalone GraphQL server in Java.

It shows the following features:
* [Datafetchers](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/main/kotlin/com/example/demo/datafetchers/ShowsDataFetcher.kt#L34)
* [Mutations](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/main/kotlin/com/example/demo/datafetchers/ReviewsDataFetcher.kt#L56) 
* [DataLoader to prevent the N+1 problem](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/main/kotlin/com/example/demo/datafetchers/ReviewsDataFetcher.kt#L46)
* [Query testing](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/test/kotlin/com/example/demo/datafetchers/ShowsDataFetcherTest.kt#L74)
* [Using a generated Query API](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/test/kotlin/com/example/demo/datafetchers/ShowsDataFetcherTest.kt#L124)  
* [File Upload](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/main/kotlin/com/example/demo/datafetchers/ArtworkUploadDataFetcher.kt#L34)
* [Using the Gradle codegen plugin](https://github.com/Netflix/dgs-examples-kotlin/blob/main/build.gradle.kts#L50)
* [A custom instrumentation implementation](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/main/kotlin/com/example/demo/instrumentation/ExampleTracingInstrumentation.kt)
* [Subscriptions](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/main/kotlin/com/example/demo/datafetchers/ReviewsDataFetcher.kt#L64)
* [Testing a subscription](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/test/kotlin/com/example/demo/datafetchers/ReviewSubscriptionTest.kt#L57)  
* [Registering an optional scalar from graphql-java](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/main/kotlin/com/example/demo/scalars/DateTimeScalarRegistration.kt#L32)

Other examples
---

There are other examples of using the DGS framework as well:

* [Java implementation of this example](https://github.com/Netflix/dgs-examples-java)
* [Federation examples (with Apollo Gateway)](https://github.com/Netflix/dgs-federation-example)

Shows and Reviews
----

This example is built around two main types: [Show](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/main/resources/schema/schema.graphqls#L14) and [Review](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/main/resources/schema/schema.graphqls#L22).
A `Show` represents a series or movie you would find on Netflix.
For ease of running the demo, the list of shows is hardcoded in [ShowsServiceImpl](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/main/kotlin/com/example/demo/services/ShowsService.kt#L32).
A show can have `Reviews`.
Again, for ease of running the demo, a list of reviews is generated during startup for each show in [DefaultReviewsService](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/main/kotlin/com/example/demo/services/ReviewsService.kt#L61).

Reviews can also be added by users of the API using a [mutation](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/main/resources/schema/schema.graphqls#L6), and a [GraphQL Subscription](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/main/resources/schema/schema.graphqls#L11) is available to watch for added reviews.

There's also a mutation available to add [Artwork](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/main/resources/schema/schema.graphqls#L7) for a show, demonstrating file uploads.
Uploaded files are stored in a folder `uploaded-images` in the work directory where ethe application is started.

Starting the example
----

The example requires Java 11.
Run the application in an IDE using its [main class](https://github.com/Netflix/dgs-examples-kotlin/blob/main/src/main/kotlin/com/example/demo/DemoApplication.kt) or using Gradle: 

```
./gradlew bootRun
```

Interact with the application using GraphiQL on http://localhost:8080/graphiql.
