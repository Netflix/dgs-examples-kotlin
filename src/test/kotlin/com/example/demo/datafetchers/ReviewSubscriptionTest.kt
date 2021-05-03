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

import com.example.demo.generated.client.AddReviewGraphQLQuery
import com.example.demo.generated.client.AddReviewProjectionRoot
import com.example.demo.generated.types.Review
import com.example.demo.generated.types.SubmittedReview
import com.example.demo.scalars.DateTimeScalarRegistration
import com.example.demo.services.DefaultReviewsService
import com.example.demo.services.ShowsService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest
import graphql.ExecutionResult
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Test the review added subscription.
 * The subscription query returns a Publisher<ExecutionResult>.
 * Each time a review is added, a new ExecutionResult is given to subscriber.
 * Normally, this publisher is consumed by the Websocket/SSE subscription handler and you don't deal with this code directly, but for testing purposes it's useful to use the stream directly.
 */
@SpringBootTest(classes = [DefaultReviewsService::class, ReviewsDataFetcher::class, DgsAutoConfiguration::class, DateTimeScalarRegistration::class])
class ReviewSubscriptionTest {
    @Autowired
    lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockBean
    lateinit var showsService: ShowsService

    @Test
    fun reviewSubscription() {
        val executionResult = dgsQueryExecutor.execute("subscription { reviewAdded(showId: 1) {starScore} }")
        val reviewPublisher = executionResult.getData<Publisher<ExecutionResult>>()
        val reviews = CopyOnWriteArrayList<Review>()

        reviewPublisher.subscribe(object: Subscriber<ExecutionResult> {
            override fun onSubscribe(s: Subscription) {
                s.request(2)
            }

            override fun onNext(t: ExecutionResult) {
                val data = t.getData<Map<String, Any>>()
                reviews.add(jacksonObjectMapper().convertValue(data["reviewAdded"], Review::class.java))
            }

            override fun onError(t: Throwable?) {
            }

            override fun onComplete() {
            }
        })

        addReview()
        addReview()

        Assertions.assertThat(reviews.size).isEqualTo(2)

    }

    private fun addReview(): ExecutionResult {
        val graphQLQueryRequest =
            GraphQLQueryRequest(
                AddReviewGraphQLQuery.Builder()
                    .review(SubmittedReview(1, "testuser", 5))
                    .build(),
                AddReviewProjectionRoot()
                    .username()
                    .starScore()
            )

        return dgsQueryExecutor.execute(graphQLQueryRequest.serialize())
    }
}