package de.pan.wichtelbot.controller

import de.pan.wichtelbot.bot.WichtelBot
import de.pan.wichtelbot.entity.ConversationStage
import de.pan.wichtelbot.entity.Participant
import de.pan.wichtelbot.repository.ParticipantRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class WichtelController(
    private val participantRepository: ParticipantRepository,
    private val wichtelBot: WichtelBot
) {
    private val log = LoggerFactory.getLogger(WichtelController::class.java)

    @GetMapping("auslosen")
    fun auslosen(): HttpEntity<*> {
        val participants = mutableListOf<Participant>()
        participants.addAll(participantRepository.findAll()
            .stream()
            .filter { p -> p.name.length > 2 }
            .filter { p -> p.address.length > 4 }
            .toList())

        participants.forEach { log.info("${it.name} ist ein Wichtelpartner.") }

        participants.shuffle()

        for (i in 0..participants.size - 2) {
            var message = "Dein:e Wichtelpartner:in ist ${participants[i + 1].name}" +
                    " und wohnt hier: ${participants[i + 1].address}"

            wichtelBot.sendTextMessage(participants[i].chatId, message)
        }

        var message = "Dein:e Wichtelpartner:in ist ${participants[0].name} und wohnt hier: ${participants[0].address}"

        wichtelBot.sendTextMessage(
            participants[participants.size - 1].chatId,
            message
        )

        return HttpEntity.EMPTY
    }
}