package com.example.swimpal.ui.screens

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.swimpal.model.Badge
import com.example.swimpal.model.UserProfile
import com.example.swimpal.viewmodel.ProfileState
import com.example.swimpal.viewmodel.UserProfileViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.viewinterop.AndroidView


@Composable
fun ProfileScreen(userProfileViewModel: UserProfileViewModel = viewModel()) {
    val profileState by userProfileViewModel.profileState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var prevBadges by remember { mutableStateOf<List<Badge>>(emptyList()) }
    var expandedData by remember { mutableStateOf(true) }
    var expandedBadges by remember { mutableStateOf(false) }
    var expandedVideo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userProfileViewModel.loadUserProfile()
    }

    LaunchedEffect(profileState) {
        if (profileState is ProfileState.Success) {
            val profile = (profileState as ProfileState.Success).userProfile
            val current = profile.badges
            val newBadges = current.filterIndexed { i, badge ->
                badge.achieved && (prevBadges.getOrNull(i)?.achieved == false)
            }
            prevBadges = current
            if (newBadges.isNotEmpty()) {
                val b = newBadges.first()
                scope.launch {
                    snackbarHostState.showSnackbar("Gratulacje! Zdobyłeś odznakę: \"${b.name}\" - ${b.description}")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text("Twój profil", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // --- DANE ---
            ExpandableCard(
                title = "Dane",
                expanded = expandedData,
                onToggle = { expandedData = !expandedData }
            ) {
                when (profileState) {
                    is ProfileState.Loading -> Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }

                    is ProfileState.Success -> {
                        val profile = (profileState as ProfileState.Success).userProfile
                        ProfileUserDataSection(
                            profile = profile,
                            onProfileChanged = { userProfileViewModel.saveUserProfile(it) }
                        )
                    }

                    is ProfileState.Error -> Text(
                        "Błąd: ${(profileState as ProfileState.Error).error}",
                        color = MaterialTheme.colorScheme.error
                    )

                    else -> Text("Brak danych profilu.")
                }
            }

            // --- ODZNAKI ---
            ExpandableCard(
                title = "Odznaki",
                expanded = expandedBadges,
                onToggle = { expandedBadges = !expandedBadges }
            ) {
                when (profileState) {
                    is ProfileState.Success -> {
                        val profile = (profileState as ProfileState.Success).userProfile
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Statystyki", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row {
                            Text("Custom: ${profile.customCount}", modifier = Modifier.weight(1f))
                            Text("Generowane: ${profile.generatedCount}", modifier = Modifier.weight(1f))
                        }
                        Row {
                            Text("Wszystkie: ${profile.totalCount}", modifier = Modifier.weight(1f))
                            Text("Dni w app: ${profile.activeDays}", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        BadgeCategoryScrollable("Customowe", profile.badges.filter { it.name.startsWith("Custom") })
                        BadgeCategoryScrollable("Generowane", profile.badges.filter { it.name.startsWith("Generated") })
                        BadgeCategoryScrollable("Wszystkie", profile.badges.filter { it.name.startsWith("Total") })
                        BadgeCategoryScrollable("Dni aktywności", profile.badges.filter { it.name.startsWith("Days") })
                    }

                    else -> Text("Brak danych odznak.")
                }
            }

            // --- WIDEO ---
            ExpandableCard(
                title = "Wideo",
                expanded = expandedVideo,
                onToggle = { expandedVideo = !expandedVideo }
            ) {
                LocalVideoPlayer(
                    resId = com.example.swimpal.R.raw.instruktaz,
                    title = "Instruktaż pływania – 18 minut",
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ExpandableCard(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text(if (expanded) "▲" else "▼")
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    content()
                }
            }
        }
    }
}

// --- KOMPOZYCJA ODTWARZACZA WIDEO (z fullscreen) ---
@Composable
fun LocalVideoPlayer(
    resId: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var isFullScreen by remember { mutableStateOf(false) }

    // Tworzenie ExoPlayera
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val uri = Uri.parse("android.resource://${context.packageName}/$resId")
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
    }

    // Zwalnianie pamięci
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = true
                        layoutParams = android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }
                },
                modifier = if (isFullScreen)
                    Modifier.fillMaxSize()
                else
                    Modifier
                        .fillMaxWidth()
                        .height(220.dp)
            )

            IconButton(
                onClick = { isFullScreen = !isFullScreen },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "Pełny ekran"
                )
            }
        }

        if (isFullScreen) {
            BackHandler {
                isFullScreen = false
            }
        }
    }
}

@Composable
private fun BadgeCategoryScrollable(kategoria: String, badges: List<Badge>) {
    if (badges.isEmpty()) return
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(kategoria, style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(6.dp))
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.Start
        ) {
            badges.forEach { BadgeItem(it) }
        }
    }
}

@Composable
fun BadgeItem(badge: Badge) {
    Card(
        modifier = Modifier
            .padding(end = 12.dp, bottom = 8.dp)
            .width(140.dp),
        colors = if (badge.achieved) CardDefaults.cardColors() else CardDefaults.outlinedCardColors()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (badge.achieved) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = "Badge Icon",
                tint = if (badge.achieved)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(badge.name, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(badge.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun ProfileUserDataSection(
    profile: UserProfile,
    onProfileChanged: (UserProfile) -> Unit
) {
    var firstName by remember(profile) { mutableStateOf(profile.firstName ?: "") }
    var lastName by remember(profile) { mutableStateOf(profile.lastName ?: "") }
    var birthDate by remember(profile) { mutableStateOf(profile.birthDate ?: "") }
    var gender by remember(profile) { mutableStateOf(profile.gender ?: "") }
    val email = profile.email ?: ""

    Column {
        OutlinedTextField(value = email, onValueChange = {}, readOnly = true, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Imię") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Nazwisko") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = birthDate, onValueChange = { birthDate = it }, label = { Text("Data urodzenia") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Płeć") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = {
                onProfileChanged(
                    profile.copy(firstName = firstName, lastName = lastName, birthDate = birthDate, gender = gender)
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zapisz zmiany")
        }
    }
}
