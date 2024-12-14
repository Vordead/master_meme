package com.mobilecampus.mastermeme.meme.presentation.screens.meme_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobilecampus.mastermeme.R
import com.mobilecampus.mastermeme.core.presentation.design_system.AppIcons
import com.mobilecampus.mastermeme.core.presentation.design_system.ObserveAsEvents
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import com.mobilecampus.mastermeme.meme.domain.model.SortOption
import com.mobilecampus.mastermeme.meme.presentation.screens.meme_list.components.MemeGrid
import com.mobilecampus.mastermeme.meme.presentation.screens.meme_list.components.MemeListTopAppBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun MemeListScreenRoot(
    onOpenEditorScreen: (resId: Int) -> Unit,
) {
    val viewModel = koinViewModel<MemeListViewModel>()
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    val templates = viewModel.templates.collectAsStateWithLifecycle().value

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is MemeListScreenEvent.OnGotoEditorScreen -> {
                event.id.toIntOrNull()?.let {
                    onOpenEditorScreen(it)
                }
                //println("Goto editor screen with id: ${event.id}")
            }
        }
    }

    MemeListScreen(
        state = state,
        onAction = viewModel::onAction,
        templates = templates
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemeListScreen(
    state: MemeListState,
    onAction: (MemeListAction) -> Unit,
    templates : List<MemeItem.Template>
) {
    var isOpen by remember { mutableStateOf(false) }
    var selectedSortOption by remember { mutableStateOf(SortOption.FAVORITES_FIRST) }
    var selectedItemsCount by remember { mutableIntStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Scaffold(
        topBar = {
            MemeListTopAppBar(
                selectedItemsCount = selectedItemsCount,
                isDropdownMenuExpanded = isOpen,
                selectedSortOption = selectedSortOption,
                onDropDownMenuClick = { isOpen = !isOpen },
                onDropdownMenuDismiss = { isOpen = false },
                onDropdownMenuItemClick = { option ->
                    selectedSortOption = option
                    isOpen = false
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(AppIcons.add, contentDescription = null, Modifier.size(32.dp), tint = Color.Black)
            }
        }
    ) { paddingValues ->
        when (state) {
            MemeListState.Empty -> EmptyMemeListState(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
            is MemeListState.Error -> TODO()
            is MemeListState.Loaded -> {
                // Using our new MemeGrid component for the created memes
                MemeGrid(
                    items = state.memes,
                    onItemClick = { meme ->
                        onAction(MemeListAction.MemeClickAction(meme.id!!))
                    },
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                )
            }
            MemeListState.Loading -> CircularProgressIndicator(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .wrapContentSize()
            )
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                TemplateSelectionContent(
                    templates = templates ,
                    onTemplateSelected = { template ->
                        onAction(MemeListAction.TemplateClickAction(template.resourceId.toString()))
                        showBottomSheet = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TemplateSelectionContent(
    templates: List<MemeItem.Template>,
    onTemplateSelected: (MemeItem.Template) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.meme_list_choose_meme),
            style = MaterialTheme.typography.headlineSmall.copy(
                textAlign = TextAlign.Center
            )
        )
        Text(
            text = stringResource(R.string.meme_list_choose_meme_description),
            modifier = Modifier.padding(vertical = 32.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.Center
            )
        )

        MemeGrid(
            items = templates,
            onItemClick = onTemplateSelected,
            columns = 2,
            contentPadding = PaddingValues(0.dp)
        )
    }
}

@Composable
fun EmptyMemeListState(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_empty_meme),
            contentDescription = "Empty state"
        )
        Text(
            text = stringResource(R.string.tap_button_to_create_your_first_meme),
            modifier = Modifier.padding(top = 34.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
        )
    }
}