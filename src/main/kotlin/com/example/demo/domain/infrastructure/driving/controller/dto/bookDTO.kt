package com.example.demo.domain.infrastructure.driving.controller.dto

import com.example.demo.domain.model.Book
import jakarta.validation.constraints.NotBlank

data class BookDto(
    @field:NotBlank
    val title: String,

    @field:NotBlank
    val author: String
)

fun BookDto.toDomain(): Book = Book(title = this.title, author = this.author)
fun Book.toDto(): BookDto = BookDto(title = this.title, author = this.author)