package de.pan.wichtelbot.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Participant(
  @Id
  val chatId: Long,
  var name: String = "",
  var address: String = "",
  var conversationStage: ConversationStage = ConversationStage.AWAITING_START
)
