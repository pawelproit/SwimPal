package com.example.swimpal.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.swimpal.model.UserProfile

/**
 * Composable displaying main screen content when user profile is loaded.
 *
 * Includes greeting, statistics, quick actions, weekly goal progress,
 * and latest achievements.
 *
 * @param profile User profile data.
 * @param onNavigateToGenerate Callback for generating a training.
 * @param onNavigateToTraining Callback for training list.
 * @param onNavigateToHistory Callback for profile/history screen.
 */

@Composable
fun MainScreenContent(
    profile: UserProfile,
    onNavigateToGenerate: (String?) -> Unit,
    onNavigateToTraining: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE1F5FE),
                        Color(0xFFF0F8FF)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            GreetingSection(userName = profile.firstName)
            Spacer(modifier = Modifier.height(24.dp))
            StatsSection(profile.totalCount, profile.activeDays)
            Spacer(modifier = Modifier.height(24.dp))
            QuickActionsSection(onNavigateToGenerate, onNavigateToTraining, onNavigateToHistory)
            Spacer(modifier = Modifier.height(24.dp))
            WeeklyGoalCard(profile.totalCount, goalTrainings = 4)
            Spacer(modifier = Modifier.height(24.dp))
            AchievementsSection(profile.badges.filter { it.achieved })
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
