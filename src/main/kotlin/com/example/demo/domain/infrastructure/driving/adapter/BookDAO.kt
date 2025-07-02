package com.example.demo.domain.infrastructure.driving.adapter

import com.example.demo.domain.model.Book
import com.example.demo.domain.port.BookRepository
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class BookDAO(
    private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate
) : BookRepository {

    override fun findAll(): List<Book> {
        return namedParameterJdbcTemplate.query(
            "SELECT * FROM BOOK",
            MapSqlParameterSource()
        ) { rs, _ ->
            Book(
                title = rs.getString("title"),
                author = rs.getString("author")
            )
        }
    }

    override fun save(book: Book) {
        namedParameterJdbcTemplate.update(
            "INSERT INTO BOOK (title, author) VALUES (:title, :author)",
            mapOf(
                "title" to book.title,
                "author" to book.author
            )
        )
    }
}