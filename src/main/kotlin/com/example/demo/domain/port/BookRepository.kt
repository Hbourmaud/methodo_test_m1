package com.example.demo.domain.port

import com.example.demo.domain.model.Book

interface BookRepository {
    fun save(book: Book)
    fun findAll(): List<Book>
}