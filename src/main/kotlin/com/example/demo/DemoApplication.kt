package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
    print(cypher('A', 2))
    runApplication<DemoApplication>(*args)
}

fun cypher(char: Char, key: Int): Char {
    require(char in 'A'..'Z') { "Only UPPERCASE Letters allowed" }
    require(key >= 0) { "key need to be positive" }

    val offset = key % 26
    val base = 'A'.code
    val original = char.code - base
    val shifted = (original + offset) % 26

    return (base + shifted).toChar()
}