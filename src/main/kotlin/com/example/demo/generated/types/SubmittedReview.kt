package com.example.demo.generated.types

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Int
import kotlin.String

public data class SubmittedReview(
  @JsonProperty("showId")
  public val showId: Int,
  @JsonProperty("username")
  public val username: String,
  @JsonProperty("starScore")
  public val starScore: Int,
) {
  public companion object
}
