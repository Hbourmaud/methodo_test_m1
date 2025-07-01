package com.example.demo.domain.usecase

import com.example.demo.domain.model.Book
import com.example.demo.domain.port.BookRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.property.arbitrary.filter
import io.mockk.*

class FakeBookRepository : BookRepository {
    private val books = mutableListOf<Book>()

    override fun save(book: Book) {
        books.add(book)
    }

    override fun findAll(): List<Book> = books.toList()
}

class BookUseCaseTest : FunSpec({

    val repository = mockk<BookRepository>(relaxUnitFun = true)
    val useCase = BookUseCase(repository)

    test("should add a book to the repository") {
        val book = Book("1984", "George Orwell")

        useCase.addBook(book)

        verify(exactly = 1) { repository.save(book) }
    }

    test("should return all books sorted by title") {
        val bookA = Book("Animal Farm", "George Orwell")
        val bookZ = Book("Zorro", "Isabel Allende")
        every { repository.findAll() } returns listOf(bookZ, bookA)

        val result = useCase.getAllBooksSortedByTitle()

        result shouldBe listOf(bookA, bookZ)
    }

    // property based test

    test("getAllBooksSortedByTitle returns all added books") {
        val titleGen = Arb.string(1..20).filter { it.isNotBlank() }
        val authorGen = Arb.string(1..20).filter { it.isNotBlank() }

        checkAll(Arb.list(titleGen, 1..10), Arb.list(authorGen, 1..10)) { titles: List<String>, authors: List<String> ->
            val books = titles.zip(authors).map { (title, author) -> Book(title, author) }

            val repo = FakeBookRepository()
            val useCase = BookUseCase(repo)

            books.forEach { useCase.addBook(it) }

            val result = useCase.getAllBooksSortedByTitle()

            result shouldContainExactlyInAnyOrder books
        }
    }

})