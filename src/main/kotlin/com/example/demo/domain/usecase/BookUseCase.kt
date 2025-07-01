package com.example.demo.domain.usecase

import com.example.demo.domain.model.Book
import com.example.demo.domain.port.BookRepository

class BookUseCase(private val repository: BookRepository) {
    fun addBook(book: Book) {
        repository.save(book)
    }

    fun getAllBooksSortedByTitle(): List<Book> {
        return repository.findAll().sortedBy { it.title }
    }
}