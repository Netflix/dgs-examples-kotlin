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

package com.example.demo.services

import com.example.demo.generated.types.Review
import com.example.demo.generated.types.SubmittedReview
import net.datafaker.Faker
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.ConnectableFlux
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import java.util.stream.IntStream
import jakarta.annotation.PostConstruct
import kotlin.streams.toList

interface ReviewsService {
    fun reviewsForShow(showId: Int): List<Review>?
    fun reviewsForShows(showIds: List<Int>): Map<Int, List<Review>>
    fun saveReview(reviewInput: SubmittedReview)
    fun getReviewsPublisher(): Publisher<Review>
}

/**
 * This service emulates a data store.
 * For convenience in the demo we just generate Reviews in memory, but imagine this would be backed by for example a database.
 * If this was indeed backed by a database, it would be very important to avoid the N+1 problem, which means we need to use a DataLoader to call this class.
 */
@Service
class DefaultReviewsService(private val showsService: ShowsService): ReviewsService {
    private val logger = LoggerFactory.getLogger(ReviewsService::class.java)

    private val reviews = mutableMapOf<Int, MutableList<Review>>()
    private lateinit var reviewsStream : FluxSink<Review>
    private lateinit var reviewsPublisher: ConnectableFlux<Review>

    @PostConstruct
    fun createReviews() {
        val faker = Faker()

        //For each show we generate a random set of reviews.
        showsService.shows().forEach { show ->
            val generatedReviews = IntStream.range(0, faker.number().numberBetween(1, 20)).mapToObj {
                val date =
                    faker.date().past(300, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                Review(
                    username = faker.name().username(),
                    starScore = faker.number().numberBetween(0, 6),
                    submittedDate = OffsetDateTime.of(date, ZoneOffset.UTC)
                )
            }.toList().toMutableList()

            reviews[show.id] = generatedReviews
        }

        val publisher = Flux.create<Review> { emitter ->
            reviewsStream = emitter
        }


        reviewsPublisher = publisher.publish()
        reviewsPublisher.connect()

    }


    /**
     * Hopefully nobody calls this for multiple shows within a single query, that would indicate the N+1 problem!
     */
    override fun reviewsForShow(showId: Int): List<Review>? {
        return reviews[showId]
    }

    /**
     * This is the method we want to call when loading reviews for multiple shows.
     * If this code was backed by a relational database, it would select reviews for all requested shows in a single SQL query.
     */
    override fun reviewsForShows(showIds: List<Int>): Map<Int, List<Review>> {
        logger.info("Loading reviews for shows ${showIds.joinToString()}")

        return reviews.filter { showIds.contains(it.key) }
    }

    override fun saveReview(reviewInput: SubmittedReview) {
        val reviewsForMovie = reviews.getOrPut(reviewInput.showId, { mutableListOf() })
        val review = Review(
            username = reviewInput.username,
            starScore = reviewInput.starScore,
            submittedDate = OffsetDateTime.now()
        )
        reviewsForMovie.add(review)
        reviewsStream.next(review)

        logger.info("Review added {}", review)
    }

    override fun getReviewsPublisher(): Publisher<Review> {
        return reviewsPublisher
    }
}
