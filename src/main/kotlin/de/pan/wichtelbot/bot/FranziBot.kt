package de.pan.wichtelbot.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.request.ChatAction
import com.pengrad.telegrambot.request.SendChatAction
import com.pengrad.telegrambot.request.SendMessage
import de.pan.wichtelbot.service.CleverbotService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import kotlinx.coroutines.*
import kotlin.random.Random

@Component
class FranziBot(
    private val cleverbotService: CleverbotService
) {
    @Value("\${app.cleverbot-token}")
    lateinit var cleverbotToken: String

    var css: MutableMap<Long, String> = mutableMapOf()

    fun handleFranzi(bot: TelegramBot, message: Message) = runBlocking {
        if(!css.containsKey(message.chat().id())) {
            css[message.chat().id()] = ""
        }

        val cs = css[message.chat().id()]
        val response = cleverbotService.think(cleverbotToken, cs!!, message.text())
        css[message.chat().id()] = response.cs

        bot.execute(SendChatAction(message.chat().id(), ChatAction.typing))

        launch {
            delay(Random.nextLong(1000, 7000))
            bot.execute(SendMessage(message.chat().id(), response.output))
        }
    }
}