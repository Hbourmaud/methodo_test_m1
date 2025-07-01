package com.example.demo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.filter
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DemoApplicationTests : FunSpec({

    test("cypher should shift letters correctly within A-Z") {
        cypher('A', 2) shouldBe 'C'
        cypher('A', 26) shouldBe 'A'
        cypher('A', 27) shouldBe 'B'
        cypher('Z', 1) shouldBe 'A'
    }

    test("cypher should throw exception for non-uppercase input") {
        shouldThrow<IllegalArgumentException> { cypher('a', 1) }
        shouldThrow<IllegalArgumentException> { cypher('1', 5) }
        shouldThrow<IllegalArgumentException> { cypher('é', 3) }
    }

    test("cypher should throw exception for negative key") {
        shouldThrow<IllegalArgumentException> { cypher('A', -1) }
    }

    // invariant tests

    test("result is always an uppercase letter") {
        checkAll(
            Arb.char().filter { it in 'A'..'Z' },
            Arb.int(0..1000)
        ) { c, key ->
            val result = cypher(c, key)
            ('A'..'Z').contains(result) shouldBe true
        }
    }

    test("cypher with key 26n returns original char") {
        checkAll(
            Arb.char().filter { it in 'A'..'Z' },
            Arb.int(0..100)
        ) { c, n ->
            cypher(c, 26 * n) shouldBe c
        }
    }

    test("cypher inverse property: cypher(cypher(c, k), 26 - k) == c") {
        checkAll(
            Arb.char().filter { it in 'A'..'Z' },
            Arb.int(0..25)
        ) { c, k ->
            val encrypted = cypher(c, k)
            val decrypted = cypher(encrypted, (26 - k) % 26)
            decrypted shouldBe c
        }
    }
})
