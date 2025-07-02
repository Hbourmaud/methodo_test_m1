package com.example.demo.infrastructure.driving

import com.example.demo.infrastructure.driving.controller.BookAlreadyExistsException
import com.example.demo.infrastructure.driving.controller.BookController
import com.example.demo.domain.model.Book
import com.example.demo.domain.usecase.BookUseCase
import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.http.MediaType.APPLICATION_JSON

@WebMvcTest(BookController::class)
@AutoConfigureMockMvc
class BookControllerIntegrationTest (
    @MockkBean private val bookUseCase: BookUseCase,
    private val mockMvc: MockMvc
) : FunSpec({
    test("get books") {
        every { bookUseCase.getAllBooksSortedByTitle() } returns listOf(Book("A", "B"))

        mockMvc.get("/api/books")
            .andExpect {
                status { isOk() }
                content { contentType(APPLICATION_JSON) }
                content {
                    json("""
                        [
                          {
                            "title": "A",
                            "author": "B"
                          }
                        ]
                        """.trimIndent()
                    )
                }
            }
    }

    test("post book") {
        justRun { bookUseCase.addBook(any()) }

        mockMvc.post("/api/books") {
            content = """
                {
                  "title": "Book title",
                  "author": "Me"
                }
            """.trimIndent()
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
        }

        verify(exactly = 1) {
            bookUseCase.addBook(Book("Book title", "Me"))
        }
    }

    test("post book should return 400 when body isnt good") {
            val result = mockMvc.post("/api/books") {
                content = """
                {
                  "title": "value"
                }
            """.trimIndent()
                contentType = APPLICATION_JSON
            }.andReturn()

        result.response.status shouldBe 400
    }

    test("post book should return 409 when book already exists") {
        every { bookUseCase.addBook(any()) } throws BookAlreadyExistsException("Book already exists")

        mockMvc.post("/api/books") {
            content = """
            {
              "title": "Existing Title",
              "author": "Existing Author"
            }
        """.trimIndent()
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isConflict() } // 409
        }

        verify(exactly = 1) { bookUseCase.addBook(Book("Existing Title", "Existing Author")) }
    }

})