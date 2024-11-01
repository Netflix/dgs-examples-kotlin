package com.example.demo.generated.types

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Int
import kotlin.String
import kotlin.collections.List

public data class Show(
  @JsonProperty("id")
  public val id: Int,
  @JsonProperty("title")
  public val title: String,
  @JsonProperty("releaseYear")
  public val releaseYear: Int? = null,
  @JsonProperty("reviews")
  public val reviews: List<Review?>? = null,
  @JsonProperty("artwork")
  public val artwork: List<Image?>? = null,
) {
  public companion object
}
