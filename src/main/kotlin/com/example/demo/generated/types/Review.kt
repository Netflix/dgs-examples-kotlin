package com.example.demo.generated.types

import com.fasterxml.jackson.`annotation`.JsonProperty
import java.time.OffsetDateTime
import kotlin.Int
import kotlin.String

public data class Review(
  @JsonProperty("username")
  public val username: String? = null,
  @JsonProperty("starScore")
  public val starScore: Int? = null,
  @JsonProperty("submittedDate")
  public val submittedDate: OffsetDateTime? = null,
) {
  public companion object
}
