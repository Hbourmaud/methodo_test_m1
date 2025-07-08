package com.example.demo.infrastructure.driving.adapter

import com.example.demo.domain.model.Book
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.ResultSet

@SpringBootTest
@ActiveProfiles("testIntegration")
class BookDAOIntegrationTest (
    private val bookDAO: BookDAO
) : FunSpec({

    beforeTest {
        hikariDataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                val hasResultSet = stmt.execute("CREATE TABLE IF NOT EXISTS book (id SERIAL PRIMARY KEY, title VARCHAR(255), author VARCHAR(255));")
                if (hasResultSet) {
                    stmt.resultSet.use { rs ->
                        rs.toList()
                    }
                }
            }
        }
        hikariDataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeUpdate("DELETE FROM book")
            }
        }
    }

    test("get all books from db") {
        hikariDataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeUpdate("""
            INSERT INTO book (title, author) VALUES
                ('untitre', 'unauteur'),
                ('Le petit prince', 'Antoine de Saint Exupery'),
                ('test', 'user')
            """.trimIndent())
            }
        }

        val books = bookDAO.findAll()

        books.shouldContainExactlyInAnyOrder(
            Book("untitre", "unauteur"),
            Book("Le petit prince", "Antoine de Saint Exupery"),
            Book("test", "user")
        )
    }

    test("create book in db") {
        bookDAO.save(Book("Nouveau titre", "nouveau auteur"))

        val results = hikariDataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                val hasResultSet = stmt.execute("SELECT * FROM book")
                if (hasResultSet) {
                    stmt.resultSet.use { rs ->
                        rs.toList()
                    }
                } else {
                    emptyList()
                }
            }
        }

        results.shouldHaveSize(1)
        assertSoftly(results.first()) {
            this["title"] shouldBe "Nouveau titre"
            this["author"] shouldBe "nouveau auteur"
        }
    }

    test("should reserve available book") {
        hikariDataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeUpdate("""
            INSERT INTO book (title, author, reserved)
            VALUES ('1984', 'Orwell', false)
        """.trimIndent())
            }
        }

        val result = bookDAO.reserveBook("1984")

        result shouldBe true
        val books = bookDAO.findAll()
        books.first().reserved shouldBe true
    }

    test("should not reserve already reserved book") {hikariDataSource.connection.use { conn ->
        conn.createStatement().use { stmt ->
            stmt.executeUpdate("""
            INSERT INTO book (title, author, reserved)
            VALUES ('1984', 'Orwell', true)
        """.trimIndent())
        }
    }

        val result = bookDAO.reserveBook("1984")

        result shouldBe false
        val books = bookDAO.findAll()
        books.first().reserved shouldBe true
    }

    afterSpec {
        container.stop()
    }
}) {

companion object {
    private val container = PostgreSQLContainer<Nothing>("postgres:13-alpine").apply {
        withDatabaseName("test")
        withUsername("test")
        withPassword("test")
        start()
        System.setProperty("spring.datasource.url", jdbcUrl)
        System.setProperty("spring.datasource.username", username)
        System.setProperty("spring.datasource.password", password)
    }

    private val hikariDataSource by lazy {
        val config = HikariConfig().apply {
            jdbcUrl = container.jdbcUrl
            username = container.username
            password = container.password
            driverClassName = container.driverClassName
        }
        HikariDataSource(config)
    }

    private fun ResultSet.toList(): List<Map<String, Any?>> {
        val md = this.metaData
        val columns = md.columnCount
        val rows = mutableListOf<Map<String, Any?>>()
        while (this.next()) {
            val row = mutableMapOf<String, Any?>()
            for (i in 1..columns) {
                row[md.getColumnName(i)] = this.getObject(i)
            }
            rows.add(row)
        }
        return rows
    }
}}