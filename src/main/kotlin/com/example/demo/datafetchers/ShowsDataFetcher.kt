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

import com.example.demo.generated.types.Show
import com.example.demo.services.ShowsService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

import kotlinx.coroutines.coroutineScope

@DgsComponent
class ShowsDataFetcher(private val showsService: ShowsService) {

    /**
     * This datafetcher resolves the `shows` field on Query.
     * It uses an @InputArgument to get the titleFilter from the Query if one is defined.
     * As an implementation detail, it leverages Kotlin Coroutines as an output type.
     *
     */
    @DgsQuery
    suspend fun shows(@InputArgument titleFilter : String?): List<Show> = coroutineScope {
        if(titleFilter != null) {
            showsService.shows().filter { it.title.contains(titleFilter) }
        } else {
            showsService.shows()
        }
    }
}