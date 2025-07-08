package com.example.demo.infrastructure.driving.adapter

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
                author = rs.getString("author"),
                reserved = rs.getBoolean("reserved")
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

    override fun reserveBook(title: String): Boolean {
        val updatedRows = namedParameterJdbcTemplate.update(
            """
        UPDATE BOOK
        SET reserved = true
        WHERE title = :title AND reserved = false
        """.trimIndent(),
            mapOf("title" to title)
        )
        return updatedRows > 0
    }
}