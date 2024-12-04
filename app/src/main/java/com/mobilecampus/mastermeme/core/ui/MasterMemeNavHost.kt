package com.mobilecampus.mastermeme.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.mobilecampus.mastermeme.meme.presentation.meme_list.navigation.MemeListNavigation
import com.mobilecampus.mastermeme.meme.presentation.meme_list.navigation.memeListDestination
import com.mobilecampus.mastermeme.meme_creation.presentation.navigation.memeCreationDestination

@Composable
fun MasterMemeNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MemeListNavigation,
        modifier = modifier
    ) {
        memeListDestination()
        memeCreationDestination()
    }
}