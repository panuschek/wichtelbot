package de.pan.wichtelbot.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.model.Chat
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup
import com.pengrad.telegrambot.request.SendMessage
import de.pan.wichtelbot.entity.ConversationStage
import de.pan.wichtelbot.entity.Participant
import de.pan.wichtelbot.repository.ParticipantRepository
import org.springframework.stereotype.Component

@Component
final class WichtelBot(
  private val telegramBot: TelegramBot,
  private val participantRepository: ParticipantRepository,
  private val franziBot: FranziBot
) : UpdatesListener {

  init {
    telegramBot.setUpdatesListener(this)
  }

  override fun process(updates: MutableList<Update>?): Int {
    updates?.forEach {
      val message = it.message()

      if(message == null)
        return@forEach

      if (message.chat().type() != Chat.Type.Private
        || message.text() == null
      )
        return@forEach

      franziBot.handleFranzi(telegramBot, it.message())

//      if (message.text().startsWith("/start")) {
//        handleConversationStart(message.chat().id())
//      } else {
//        val participant = participantRepository.findByChatId(message.chat().id())
//        when (participant?.conversationStage) {
//            ConversationStage.AWAITING_INITIAL_RESPONSE -> {
//              handleInitialResponse(participant, it.message())
//            }
//            ConversationStage.AWAITING_USERNAME -> {
//              handleUsernameResponse(participant, it.message())
//            }
//            ConversationStage.AWAITING_ADDRESS -> {
//              handleAddressResponse(participant, it.message())
//            }
//          else -> {
//            if(message.chat().id() == 248206910L) {
//              franziBot.handleFranzi(telegramBot, it.message())
//            }
//          }
//        }
//      }
    }

    return UpdatesListener.CONFIRMED_UPDATES_ALL
  }

  private fun handleAddressResponse(participant: Participant, message: Message) {
    if(message.text().length < 5) {
      telegramBot.execute(getTextMessage(participant.chatId, "Das ist bischen kurz für eine Adresse, versuch's nochmal."))
      return
    }

    participant.address = message.text()
    participant.conversationStage = ConversationStage.READY
    participantRepository.save(participant)

    telegramBot.execute(getTextMessage(participant.chatId, "Du bist dabei, ${participant.name}. Ich schicke dir bald eine Nachricht, wer dein:e Partner:in ist." +
            " Wenn du es dir doch anders überlegst, schreib mich mit /start an und wähle \"Nein\" aus."))
  }

  private fun handleUsernameResponse(participant: Participant, message: Message) {
    if(message.text().length < 3) {
      telegramBot.execute(getTextMessage(participant.chatId, "Bitte gib wenigstens drei Buchstaben ein."))
      return
    }

    participant.name = message.text()
    participant.conversationStage = ConversationStage.AWAITING_ADDRESS
    participantRepository.save(participant)

    telegramBot.execute(getTextMessage(participant.chatId, "Bitte schick mir jetzt noch deine Anschrift," +
            " sodass dein:e Partner:in dir dein Geschenk auch schicken kann, falls ihr euch nicht persönlich seht."))
  }

  private fun handleInitialResponse(participant: Participant, message: Message) {
    if(message.text().lowercase() == "ja") {
      telegramBot.execute(getAskUsernameMessage(participant.chatId))
      participant.conversationStage = ConversationStage.AWAITING_USERNAME
      participantRepository.save(participant)
    } else if(message.text().lowercase() == "nein") {
      telegramBot.execute(getDontParticipateMessage(participant.chatId))
      participantRepository.deleteById(participant.chatId)
    } else {
      telegramBot.execute(getTextMessage(participant.chatId, "Antworte Ja oder Nein."))
    }
  }

  private fun getDontParticipateMessage(chatId: Long): SendMessage {
    return SendMessage(
      chatId,
      "Ich habe dich aus der Teilnehmerliste gelöscht. Schreib mir /start falls du doch teilnehmen möchtest."
    )
  }

  private fun handleConversationStart(chatId: Long) {
    val participant = participantRepository.findByChatId(chatId) ?: Participant(chatId)

    telegramBot.execute(getStartMessage(chatId))
    participant.conversationStage = ConversationStage.AWAITING_INITIAL_RESPONSE
    participantRepository.save(participant)
  }

  private fun getAskUsernameMessage(chatId: Long): SendMessage {
    return getTextMessage(
      chatId,
      "Wie ist dein Name?"
    )
  }

  private fun getStartMessage(chatId: Long): SendMessage {
    return getTextMessage(
      chatId,
      "Möchtest du beim Wichteln mitmachen?"
    )
      .replyMarkup(getParticipationKeyboard())
  }

  private fun getTextMessage(chatId: Long, message: String): SendMessage {
    return SendMessage(chatId, message)
  }

  fun sendTextMessage(chatId: Long, message: String) {
    telegramBot.execute(getTextMessage(chatId, message))
  }

  private fun getParticipationKeyboard(): ReplyKeyboardMarkup {
    return ReplyKeyboardMarkup(
      arrayOf("Ja"),
      arrayOf("Nein")
    )
      .oneTimeKeyboard(true)
      .resizeKeyboard(true)
  }
}