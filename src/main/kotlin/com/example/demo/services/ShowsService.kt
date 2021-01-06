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

import com.example.demo.generated.types.Show
import org.springframework.stereotype.Service

interface ShowsService {
    fun shows(): List<Show>
}

/**
 * This service gives a fixed in-memory collection of Shows.
 * In a more realistic implementation the Shows could be loaded from a datastore.
 */
@Service
class BasicShowsService : ShowsService {
    override fun shows(): List<Show> {
        return listOf(
            Show(id = 1, title = "Stranger Things", releaseYear = 2016),
            Show(id = 2, title = "Ozark", releaseYear = 2017),
            Show(id = 3, title = "The Crown", releaseYear = 2016),
            Show(id = 4, title = "Dead to Me", releaseYear = 2019),
            Show(id = 5, title = "Orange is the New Black", releaseYear = 2013)
        )
    }
}