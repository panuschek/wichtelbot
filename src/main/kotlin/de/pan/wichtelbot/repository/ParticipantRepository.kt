package de.pan.wichtelbot.repository

import de.pan.wichtelbot.entity.ConversationStage
import de.pan.wichtelbot.entity.Participant
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ParticipantRepository : MongoRepository<Participant, Long> {
  fun findByChatId(chatId: Long): Participant?
  fun getByConversationStage(conversationStage: ConversationStage): MutableList<Participant>
}