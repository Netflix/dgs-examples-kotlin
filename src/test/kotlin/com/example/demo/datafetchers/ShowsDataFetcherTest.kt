/*
 * Copyright 2021 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demo.datafetchers

import com.example.demo.dataloaders.ReviewsDataLoader
import com.example.demo.generated.client.AddReviewGraphQLQuery
import com.example.demo.generated.client.AddReviewProjectionRoot
import com.example.demo.generated.client.ShowsGraphQLQuery
import com.example.demo.generated.client.ShowsProjectionRoot
import com.example.demo.generated.types.Review
import com.example.demo.generated.types.Show
import com.example.demo.generated.types.SubmittedReview
import com.example.demo.generated.types.TitleFormat
import com.example.demo.scalars.DateTimeScalarRegistration
import com.example.demo.services.ReviewsService
import com.example.demo.services.ShowsService
import com.jayway.jsonpath.TypeRef
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.OffsetDateTime

@SpringBootTest(classes = [ShowsDataFetcher::class, ReviewsDataFetcher::class, ReviewsDataLoader::class, DgsAutoConfiguration::class, DateTimeScalarRegistration::class])
class ShowsDataFetcherTest {

    @Autowired
    lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockBean
    lateinit var showsService: ShowsService

    @MockBean
    lateinit var reviewsService: ReviewsService

    @BeforeEach
    fun before() {
        `when`(showsService.shows()).thenAnswer { listOf(Show(id = 1, title = "mock title", releaseYear = 2020)) }
        `when`(reviewsService.reviewsForShows(listOf(1))).thenAnswer {
            mapOf(
                Pair(
                    1, listOf(
                        Review("DGS User", 5, OffsetDateTime.now()),
                        Review("DGS User 2", 3, OffsetDateTime.now()),
                    )
                )
            )
        }
    }

    @Test
    fun shows() {
        val titles: List<String> = dgsQueryExecutor.executeAndExtractJsonPath(
            """
            {
                shows {
                    title
                    releaseYear
                }
            }
        """.trimIndent(), "data.shows[*].title"
        )

        assertThat(titles).contains("mock title")
    }

    @Test
    @Disabled("Unstable test in Github Actions")
    fun showsWithException() {
        `when`(showsService.shows()).thenThrow(RuntimeException("nothing to see here"))

        val result = dgsQueryExecutor.execute(
            """
            {
                shows {
                    title
                    releaseYear
                }
            }
        """.trimIndent()
        )

        assertThat(result.errors).isNotEmpty
        assertThat(result.errors[0].message).isEqualTo("java.lang.RuntimeException: nothing to see here")
    }

    @Test
    fun showsWithQueryApi() {
        val graphQLQueryRequest =
            GraphQLQueryRequest(
                ShowsGraphQLQuery.Builder()
                    .build(),
                ShowsProjectionRoot().title()
            )
        val titles = dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
            graphQLQueryRequest.serialize(),
            "data.shows[*].title"
        )
        assertThat(titles).contains("mock title")
    }

    @Test
    fun showWithReviews() {
        val graphQLQueryRequest =
            GraphQLQueryRequest(
                ShowsGraphQLQuery.Builder()
                    .build(),
                ShowsProjectionRoot()
                    .title(TitleFormat(uppercase = true)).parent
                    .reviews()
                    .username()
                    .starScore()
            )
        val shows = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            graphQLQueryRequest.serialize(),
            "data.shows[*]",
            object : TypeRef<List<Show>>() {})
        assertThat(shows.size).isEqualTo(1)
        assertThat(shows[0].reviews?.size).isEqualTo(2)
    }

    @Test
    fun addReviewMutation() {

        val graphQLQueryRequest =
            GraphQLQueryRequest(
                AddReviewGraphQLQuery.Builder()
                    .review(SubmittedReview(1, "testuser", 5))
                    .build(),
                AddReviewProjectionRoot()
                    .username()
                    .starScore()
            )

        val executionResult = dgsQueryExecutor.execute(graphQLQueryRequest.serialize())
        assertThat(executionResult.errors).isEmpty()

        verify(reviewsService).reviewsForShow(1)
    }
}