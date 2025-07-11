﻿package com.example.demo.infrastructure.driving.controller

import com.example.demo.domain.usecase.BookUseCase
import com.example.demo.infrastructure.driving.controller.dto.BookDto
import com.example.demo.infrastructure.driving.controller.dto.toDomain
import com.example.demo.infrastructure.driving.controller.dto.toDto
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/books")
class BookController(
    private val bookUseCase: BookUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBook(@RequestBody @Valid dto: BookDto) {
        bookUseCase.addBook(dto.toDomain())
    }

    @GetMapping
    fun getAllBooks(): List<BookDto> {
        return bookUseCase.getAllBooksSortedByTitle().map { it.toDto() }
    }

    @PostMapping("/{title}/reserve")
    fun reserveBook(@PathVariable title: String): ResponseEntity<String> {
        return if (bookUseCase.reserveBook(title)) {
            ResponseEntity.ok("Book reserved")
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT).body("Book is already reserved")
        }
    }
}