package com.mobilecampus.mastermeme.meme.presentation.screens.meme_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import com.mobilecampus.mastermeme.meme.domain.model.SortOption
import com.mobilecampus.mastermeme.meme.domain.use_case.DeleteMemeUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.GetMemesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.GetTemplatesUseCase
import com.mobilecampus.mastermeme.meme.domain.use_case.ToggleFavoriteUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MemeListViewModel(
    private val getMemesUseCase: GetMemesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val deleteMemesUseCase: DeleteMemeUseCase,
    private val getTemplatesUseCase: GetTemplatesUseCase
) : ViewModel() {
    // Keep track of templates separately to avoid reloading them unnecessarily
    private val _templates = MutableStateFlow<List<MemeItem.Template>>(emptyList())
    val templates = _templates
        .asStateFlow()


    private val _uiState = MutableStateFlow<MemeListState>(MemeListState.Loading)
    val uiState = _uiState
        .onStart {
            loadMemes()
            loadTemplates()
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MemeListState.Loading
        )

    private val eventChannel = Channel<MemeListScreenEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()


    private fun loadTemplates() {
        viewModelScope.launch {
            try {
                _templates.value = getTemplatesUseCase()
            } catch (e: Exception) {
                // Handle template loading error if needed
                // You might want to show a toast or handle this differently
            }
        }
    }

    fun onAction(action: MemeListAction) {
        viewModelScope.launch {
            when (action) {
                is MemeListAction.MemeClickAction -> {
                    eventChannel.send(
                        MemeListScreenEvent.OnGotoEditorScreen(action.id.toString())
                    )
                }

                is MemeListAction.TemplateClickAction -> {
                    // Handle template clicks the same way as meme clicks
                    eventChannel.send(
                        MemeListScreenEvent.OnGotoEditorScreen(action.templateId)
                    )
                }
                is MemeListAction.ToggleFavoriteAction -> {
                    toggleFavoriteUseCase(action.meme.id!!)
                }
            }
        }
    }

    private fun loadMemes() {
        viewModelScope.launch {
            try {
                _uiState.update { MemeListState.Loading }
                delay(2000)
                getMemesUseCase(SortOption.FAVORITES_FIRST).collect { memes ->
                    _uiState.value = if (memes.isEmpty()) {
                        MemeListState.Empty
                    } else {
                        MemeListState.Loaded(
                            memes = memes,
                            sortMode = SortOption.FAVORITES_FIRST
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { MemeListState.Error("Failed to load memes") }
            }
        }
    }
}

// Add a new action for template selection
sealed interface MemeListAction {
    data class ToggleFavoriteAction(val meme: MemeItem.ImageMeme) : MemeListAction
    data class MemeClickAction(val id: Int) : MemeListAction
    data class TemplateClickAction(val templateId: String) : MemeListAction
}

// First, let's update the state to include templates
sealed class MemeListState {
    data object Loading : MemeListState()
    data class Loaded(
        val memes: List<MemeItem.ImageMeme>,
        val sortMode: SortOption = SortOption.FAVORITES_FIRST,
        val selectionMode: Boolean = false,
        val selectedMemes: Set<MemeItem.ImageMeme> = setOf()
    ) : MemeListState()

    data class Error(val message: String) : MemeListState()
    data object Empty : MemeListState()
}


sealed interface MemeListScreenEvent {
    data class OnGotoEditorScreen(val id: String) : MemeListScreenEvent
}