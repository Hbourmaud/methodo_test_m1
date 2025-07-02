package com.example.demo.domain.infrastructure.application

import com.example.demo.domain.infrastructure.driving.adapter.BookDAO
import com.example.demo.domain.port.BookRepository
import com.example.demo.domain.usecase.BookUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCaseConfig {

    @Bean
    fun bookUseCase(bookDAO: BookDAO): BookUseCase {
        return BookUseCase(bookDAO)
    }
}