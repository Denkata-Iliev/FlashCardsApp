package com.example.flashcardsapp.ui.deck

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.data.entity.Deck
import com.example.flashcardsapp.data.entity.DeckCards
import com.example.flashcardsapp.data.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DeckListViewModel(private val deckRepository: DeckRepository) : ViewModel() {
    val deckListUiState: StateFlow<DeckListUiState> =
        deckRepository.getAll().map { DeckListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT),
                initialValue = DeckListUiState()
            )

    var createDeckUiState by mutableStateOf(CreateDeckUiState())
        private set

    var showCreateDialog: Boolean by mutableStateOf(false)
        private set

    var showUpdateDialog: Boolean by mutableStateOf(false)
        private set

    var showDeleteConfirm: Boolean by mutableStateOf(false)
        private set

    private val _selectedIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedIds = _selectedIds.asStateFlow()

    private val _inSelectionMode = MutableStateFlow(false)
    val inSelectionMode = _inSelectionMode.asStateFlow()

    var deckToUpdate by mutableStateOf<Deck?>(null)
        private set

    fun addDeckToSelection(id: Int) {
        _selectedIds.value += id
    }

    fun addRangeToSelection(k1: Int, k2: Int, k3: Int) {
        _selectedIds.value = _selectedIds.value
            .minus(k1..k2)
            .minus(k2..k1)
            .plus(k1..k3)
            .plus(k3..k1)
    }

    fun toggleDeckSelected(id: Int, selected: Boolean) {
        if (selected) {
            _selectedIds.value += id
        } else {
            _selectedIds.value -= id
        }
    }

    fun selectAll(decks: List<Deck>) {
        _selectedIds.value = List(decks.size) { it }.toSet()
    }

    fun deselectAll() {
        _selectedIds.value = emptySet()
    }

    private fun updateSelectionMode(newValue: Boolean) {
        _inSelectionMode.value = newValue  // Set the value normally
        _inSelectionMode.value = !_inSelectionMode.value  // Flip it
        _inSelectionMode.value = newValue  // Set it back
    }

    fun enterSelectionMode() {
        updateSelectionMode(true)
    }

    fun exitSelectionMode() {
        updateSelectionMode(false)
        _selectedIds.value = emptySet()
    }

    fun openDeleteConfirm() {
        showDeleteConfirm = true
    }

    fun closeDeleteConfirm() {
        showDeleteConfirm = false
    }

    fun openCreateDialog() {
        showCreateDialog = true
    }

    fun closeCreateDialog() {
        showCreateDialog = false
        resetCreateUiState()
    }

    fun openUpdateDialog() {
        viewModelScope.launch {
            val deckIndex = _selectedIds.value.first()
            deckToUpdate = getById(deckListUiState.value.decks[deckIndex].id)
            showUpdateDialog = true
            updateCreateUiState(deckToUpdate!!.id, deckToUpdate!!.name)
        }
    }

    fun closeUpdateDialog() {
        showUpdateDialog = false
        resetCreateUiState()
    }

    fun updateCreateUiState(deckName: String) {
        createDeckUiState = createDeckUiState.copy(
            deckName = deckName,
            errorMessage = null
        )
    }

    fun deleteByIds(ids: Set<Int>) {
        viewModelScope.launch {
            val decks = ids.map {
                deckRepository.getById(deckListUiState.value.decks[it].id)
            }.toTypedArray()

            deckRepository.deleteAll(*decks)

            closeDeleteConfirm()
            exitSelectionMode()
        }
    }

    fun updateDeck() {
        if (!validateInput()) {
            return
        }

        viewModelScope.launch {
            if (deckRepository.existsByName(createDeckUiState.deckName.trim())) {
                createDeckUiState = createDeckUiState.copy(errorMessage = "This deck already exists!")
                return@launch
            }

            deckRepository.update(Deck(createDeckUiState.id, createDeckUiState.deckName.trim()))
            createDeckUiState = CreateDeckUiState()
            closeUpdateDialog()
            exitSelectionMode()
        }

    }

    fun createDeck() {
        if (!validateInput()) {
            return
        }

        viewModelScope.launch {
            if (deckRepository.existsByName(createDeckUiState.deckName.trim())) {
                createDeckUiState = createDeckUiState.copy(errorMessage = "This deck already exists!")
                return@launch
            }

            deckRepository.insertAll(Deck(0, createDeckUiState.deckName.trim()))
            createDeckUiState = CreateDeckUiState()
            closeCreateDialog()
        }
    }

    private fun validateInput(createUiState: CreateDeckUiState = createDeckUiState): Boolean {
        return with(createUiState) {
            if (deckName.trim().isBlank()) {
                createDeckUiState = createUiState.copy(errorMessage = "Deck name cannot be blank!")
                return false
            }

            if (deckName.trim().length > 15) {
                createDeckUiState =
                    createUiState.copy(errorMessage = "Deck name cannot be more than 15 characters long!")
                return false
            }

            true
        }
    }

    private fun updateCreateUiState(id: Int, deckName: String) {
        createDeckUiState = createDeckUiState.copy(
            id = id,
            deckName = deckName
        )
    }

    private fun resetCreateUiState() {
        createDeckUiState = CreateDeckUiState()
    }

    private suspend fun getById(id: Int) = deckRepository.getById(id)

    private val jsonFormatter = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun exportSelectedDecks(context: Context, uri: Uri) {
        viewModelScope.launch {
            val selectedDecksIds = _selectedIds.value.map {
                deckListUiState.value.decks[it].id
            }

            val selectedDecks = deckRepository.getDecksWithCardsById(selectedDecksIds)

            val exportable = selectedDecks.map { it.toExportableDeck() }

            val json = jsonFormatter.encodeToString(exportable)

            context.contentResolver.openOutputStream(uri)?.use {
                it.write(json.toByteArray())
            }
        }
    }

    fun importFromJson(context: Context, uri: Uri) {
        viewModelScope.launch {
            val input = context.contentResolver.openInputStream(uri) ?: return@launch
            val json = input.bufferedReader().use { it.readText() }

            val decks: List<ExportableDeck> = jsonFormatter.decodeFromString(json)

            deckRepository.insertDecksWithCards(decks)
        }
    }

    companion object {
        private const val TIMEOUT = 5000L
    }
}

data class DeckListUiState(val decks: List<Deck> = listOf())

data class CreateDeckUiState(
    val id: Int = 0,
    val deckName: String = "",
    val errorMessage: String? = null
)

@Serializable
data class ExportableDeck(val name: String, val cards: List<ExportableCard>)

@Serializable
data class ExportableCard(val question: String, val answer: String)

fun DeckCards.toExportableDeck(): ExportableDeck = ExportableDeck(
    name = this.deck.name,
    cards = this.cards.map { it.toExportableCard() }
)

fun Card.toExportableCard(): ExportableCard = ExportableCard(
    question = this.question,
    answer = this.answer
)