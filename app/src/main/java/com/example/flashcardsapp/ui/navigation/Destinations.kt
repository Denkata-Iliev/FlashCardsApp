package com.example.flashcardsapp.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object StartDestination

@Serializable
data class CardListDestination(val deckId: Int = 0)