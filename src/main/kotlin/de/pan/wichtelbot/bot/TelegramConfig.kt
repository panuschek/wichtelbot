package de.pan.wichtelbot.bot

import com.pengrad.telegrambot.TelegramBot
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TelegramConfig {

  @Value("\${app.bot-token}")
  lateinit var botToken: String

  @Bean
  fun telegramBot(): TelegramBot {
    return TelegramBot(botToken)
  }
}