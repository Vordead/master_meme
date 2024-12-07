package com.mobilecampus.mastermeme.meme.presentation.meme_list.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mobilecampus.mastermeme.core.design_system.AppTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemeListTopAppBar(
    selectedItemsCount: Int,
    isDropdownMenuExpanded: Boolean,
    selectedSortOption: SortOption,
    onDropDownMenuClick: () -> Unit,
    onDropdownMenuDismiss: () -> Unit,
    onDropdownMenuItemClick: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    AppTopAppBar(
        title = if (selectedItemsCount > 0) selectedItemsCount.toString() else "Your memes",
        navigationIcon = {
            if (selectedItemsCount > 0) {
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.Close, contentDescription = null)
                }
            }
        },
        actions = {
            if (selectedItemsCount > 0) {
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.Share, contentDescription = null)
                }
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(Icons.Outlined.Delete, contentDescription = null)
                }
            } else {
                TextButton(
                    onClick = onDropDownMenuClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.3f)
                    )
                ) {
                    Text(selectedSortOption.displayName)
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = isDropdownMenuExpanded,
                    onDismissRequest = onDropdownMenuDismiss
                ) {
                    SortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.displayName) },
                            onClick = { onDropdownMenuItemClick(option) }
                        )
                    }
                }
            }
        },
        modifier = modifier
    )
}

enum class SortOption(val displayName: String) {
    FAVORITES_FIRST("Favorites First"),
    NEWEST_FIRST("Newest First")
}