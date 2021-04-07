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

import com.example.demo.generated.DgsConstants
import com.example.demo.generated.types.Image
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.streams.toList

@DgsComponent
class ArtworkUploadDataFetcher {
    @DgsMutation
    fun addArtwork(@InputArgument showId: Int, @InputArgument upload: MultipartFile): List<Image> {
        val uploadDir = Paths.get("uploaded-images")
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir)
        }

        Files.newOutputStream(
            uploadDir.resolve(
                "show-$showId-${UUID.randomUUID()}.${upload.originalFilename?.substringAfterLast(".")}"
            )
        ).use { it.write(upload.bytes) }

        return Files.list(uploadDir)
            .filter { it.fileName.toString().startsWith("show-$showId-") }
            .map { it.fileName.toString() }
            .map { Image(url = it) }.toList()
    }
}