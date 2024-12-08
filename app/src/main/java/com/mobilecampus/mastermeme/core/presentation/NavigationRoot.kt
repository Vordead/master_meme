package com.mobilecampus.mastermeme.core.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mobilecampus.mastermeme.core.presentation.ui.MasterMemeBackground
import com.mobilecampus.mastermeme.meme.presentation.screens.list.MemeListScreen
import kotlinx.serialization.Serializable

// Navigation Destinations
sealed interface NavGraph {
    @Serializable data object MemeList: NavGraph
}

@Composable
fun NavigationRoot(
    innerPadding: PaddingValues,
    navController: NavHostController,
) {
    MasterMemeBackground {
        NavHost(
            navController = navController,
            startDestination = NavGraph.MemeList,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding(),
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                    )
                )
        ) {
            composable<NavGraph.MemeList> {
                MemeListScreen()
            }
        }
    }
}