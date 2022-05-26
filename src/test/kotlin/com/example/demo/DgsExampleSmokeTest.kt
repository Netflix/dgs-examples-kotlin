package com.example.demo

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

/**
 * Example of a smoke test that will interact with the HTTP /graphql endpoint via MockMVC
 */
@SpringBootTest
@EnableAutoConfiguration
@AutoConfigureMockMvc
class DgsExampleSmokeTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Test
    fun `Queries for shows`() {
        mvc.perform(
            MockMvcRequestBuilders
                .post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        | { 
                        |   "query": "query some_movies { shows { title releaseYear } }" 
                        | }""".trimMargin()
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.content().json(
                    """
                    | {
                    |  "data": {
                    |    "shows":[
                    |      { "title":"Stranger Things", "releaseYear":2016 },
                    |      { "title":"Ozark", "releaseYear":2017 },
                    |      { "title":"The Crown","releaseYear":2016 },
                    |      {"title":"Dead to Me","releaseYear":2019},
                    |      {"title":"Orange is the New Black","releaseYear":2013}
                    |    ]
                    |  }
                    |}
                    |""".trimMargin(),
                    false
                )
            )
    }
}