package com.example.demo.infrastructure.application

import com.example.demo.infrastructure.driving.adapter.BookDAO
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