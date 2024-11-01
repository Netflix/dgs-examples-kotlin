package com.example.demo.generated

import kotlin.String

public object DgsConstants {
  public const val QUERY_TYPE: String = "Query"

  public const val Mutation_TYPE: String = "Mutation"

  public const val Subscription_TYPE: String = "Subscription"

  public object QUERY {
    public const val TYPE_NAME: String = "Query"

    public const val Shows: String = "shows"

    public object SHOWS_INPUT_ARGUMENT {
      public const val TitleFilter: String = "titleFilter"
    }
  }

  public object MUTATION {
    public const val TYPE_NAME: String = "Mutation"

    public const val AddReview: String = "addReview"

    public const val AddArtwork: String = "addArtwork"

    public object ADDREVIEW_INPUT_ARGUMENT {
      public const val Review: String = "review"
    }

    public object ADDARTWORK_INPUT_ARGUMENT {
      public const val ShowId: String = "showId"

      public const val Upload: String = "upload"
    }
  }

  public object SUBSCRIPTION {
    public const val TYPE_NAME: String = "Subscription"

    public const val ReviewAdded: String = "reviewAdded"

    public object REVIEWADDED_INPUT_ARGUMENT {
      public const val ShowId: String = "showId"
    }
  }

  public object SHOW {
    public const val TYPE_NAME: String = "Show"

    public const val Id: String = "id"

    public const val Title: String = "title"

    public const val ReleaseYear: String = "releaseYear"

    public const val Reviews: String = "reviews"

    public const val Artwork: String = "artwork"

    public object TITLE_INPUT_ARGUMENT {
      public const val Format: String = "format"
    }
  }

  public object REVIEW {
    public const val TYPE_NAME: String = "Review"

    public const val Username: String = "username"

    public const val StarScore: String = "starScore"

    public const val SubmittedDate: String = "submittedDate"
  }

  public object IMAGE {
    public const val TYPE_NAME: String = "Image"

    public const val Url: String = "url"
  }

  public object TITLEFORMAT {
    public const val TYPE_NAME: String = "TitleFormat"

    public const val Uppercase: String = "uppercase"
  }

  public object SUBMITTEDREVIEW {
    public const val TYPE_NAME: String = "SubmittedReview"

    public const val ShowId: String = "showId"

    public const val Username: String = "username"

    public const val StarScore: String = "starScore"
  }
}
