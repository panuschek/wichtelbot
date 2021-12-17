package de.pan.wichtelbot.entity

enum class ConversationStage {
  AWAITING_INITIAL_RESPONSE,
  AWAITING_USERNAME,
  AWAITING_START,
  AWAITING_ADDRESS,
  READY
}