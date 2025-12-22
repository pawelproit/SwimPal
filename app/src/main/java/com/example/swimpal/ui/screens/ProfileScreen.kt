package com.example.swimpal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swimpal.model.Training
import com.example.swimpal.model.UserProfile
import com.example.swimpal.viewmodel.ProfileState
import com.example.swimpal.viewmodel.UserProfileViewModel
import com.example.swimpal.viewmodel.TrainingViewModel
import com.example.swimpal.ui.components.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfileViewModel: UserProfileViewModel = viewModel(),
    trainingViewModel: TrainingViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val profileState by userProfileViewModel.profileState.collectAsState()

    var expandedData by remember { mutableStateOf(false) }
    var expandedBadges by remember { mutableStateOf(false) }
    var expandedVideo by remember { mutableStateOf(false) }
    var expandedHistory by remember { mutableStateOf(false) }

    val historyTrainings by trainingViewModel.historyTrainings.collectAsState(initial = emptyList())
    val expandedHistoryTrainings = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(Unit) {
        userProfileViewModel.loadUserProfile()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
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
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Top,
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        Text(
                            text = "👤 Twój profil",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Zarządzaj swoim kontem",
                            fontSize = 16.sp,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    item {
                        ExpandableCard(
                            title = "📝 Dane osobowe",
                            expanded = expandedData,
                            onToggle = { expandedData = !expandedData }
                        ) {
                            when (profileState) {
                                is ProfileState.Loading -> Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Color(0xFF2196F3))
                                }
                                is ProfileState.Success -> {
                                    ProfileUserDataSection(
                                        profile = (profileState as ProfileState.Success).userProfile,
                                        onProfileChanged = { userProfileViewModel.saveUserProfile(it) }
                                    )
                                }
                                is ProfileState.Error -> Text(
                                    "Błąd: ${(profileState as ProfileState.Error).error}",
                                    color = Color.Red,
                                    fontSize = 14.sp
                                )
                                else -> Text("Brak danych profilu.", fontSize = 14.sp)
                            }
                        }
                    }

                    item {
                        ExpandableCard(
                            title = "🏆 Odznaki i osiągnięcia",
                            expanded = expandedBadges,
                            onToggle = { expandedBadges = !expandedBadges }
                        ) {
                            when (profileState) {
                                is ProfileState.Success -> {
                                    val profile = (profileState as ProfileState.Success).userProfile
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "📊 Statystyki",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row {
                                        Text(
                                            "Custom: ${profile.customCount}",
                                            modifier = Modifier.weight(1f),
                                            color = Color(0xFF666666),
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            "Generowane: ${profile.generatedCount}",
                                            modifier = Modifier.weight(1f),
                                            color = Color(0xFF666666),
                                            fontSize = 14.sp
                                        )
                                    }
                                    Row {
                                        Text(
                                            "Wszystkie: ${profile.totalCount}",
                                            modifier = Modifier.weight(1f),
                                            color = Color(0xFF666666),
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            "Dni w app: ${profile.activeDays}",
                                            modifier = Modifier.weight(1f),
                                            color = Color(0xFF666666),
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    BadgeCategoryScrollable(
                                        "💪 Customowe",
                                        profile.badges.filter { it.name.startsWith("Custom") }
                                    )
                                    BadgeCategoryScrollable(
                                        "🤖 Generowane",
                                        profile.badges.filter { it.name.startsWith("Generated") }
                                    )
                                    BadgeCategoryScrollable(
                                        "🎯 Wszystkie",
                                        profile.badges.filter { it.name.startsWith("Total") }
                                    )
                                    BadgeCategoryScrollable(
                                        "📅 Dni aktywności",
                                        profile.badges.filter { it.name.startsWith("Days") }
                                    )
                                }
                                else -> Text("Brak danych odznak.", fontSize = 14.sp)
                            }
                        }
                    }

                    item {
                        ExpandableCard(
                            title = "🎥 Instruktaż wideo",
                            expanded = expandedVideo,
                            onToggle = { expandedVideo = !expandedVideo }
                        ) {
                            VideoSection()
                        }
                    }

                    item {
                        ExpandableCard(
                            title = "📜 Historia treningów",
                            expanded = expandedHistory,
                            onToggle = { expandedHistory = !expandedHistory }
                        ) {
                            TrainingHistorySection(
                                historyTrainings = historyTrainings,
                                expandedHistoryTrainings = expandedHistoryTrainings
                            )
                        }
                    }
                }

                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(
                        text = "🚪 Wyloguj się",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileUserDataSection(
    profile: UserProfile,
    onProfileChanged: (UserProfile) -> Unit
) {
    var firstName by remember { mutableStateOf(profile.firstName) }
    var lastName by remember { mutableStateOf(profile.lastName) }
    var birthDate by remember { mutableStateOf(profile.birthDate) }
    var gender by remember { mutableStateOf(profile.gender) }

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }

    var expandedGender by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val displayDateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    val storageDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun parseBirthDate(dateStr: String): Long? {
        return try {
            val date = storageDateFormat.parse(dateStr)
            date?.time
        } catch (e: Exception) {
            null
        }
    }

    LaunchedEffect(birthDate) {
        selectedDateMillis = parseBirthDate(birthDate)
    }

    val displayBirthDate = if (birthDate.isNotBlank()) {
        try {
            val date = storageDateFormat.parse(birthDate)
            displayDateFormat.format(date ?: Date())
        } catch (e: Exception) {
            birthDate
        }
    } else ""

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Imię") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Nazwisko") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = displayBirthDate,
            onValueChange = { },
            label = { Text("Data urodzenia") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            readOnly = true,
            singleLine = true
        )



        ExposedDropdownMenuBox(
            expanded = expandedGender,
            onExpandedChange = { expandedGender = !expandedGender }
        ) {
            OutlinedTextField(
                value = gender.ifEmpty { "Wybierz płeć" },
                onValueChange = { },
                readOnly = true,
                label = { Text("Płeć") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = if (expandedGender) Color.Blue else Color.Gray
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedGender,
                onDismissRequest = { expandedGender = false }
            ) {
                listOf("Mężczyzna", "Kobieta", "Inne").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            gender = option
                            expandedGender = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Button(
            onClick = {
                val updatedProfile = profile.copy(
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    birthDate = birthDate,
                    gender = gender
                )
                onProfileChanged(updatedProfile)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("💾 Zapisz zmiany")
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)

        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("Wybierz datę urodzenia") },
            text = {
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Date(millis)
                            birthDate = storageDateFormat.format(date)
                            selectedDateMillis = millis
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
}

@Composable
private fun VideoSection() {
    val baseUrl = "https://pawelproit.github.io/swimpal-videos/videos"

    data class VideoItem(
        val key: String,
        val title: String,
        val url: String
    )

    val videoCategories: Map<String, List<VideoItem>> = mapOf(
        "Kraul" to listOf(
            VideoItem("kraul_pelny", "Kraul pełny", "$baseUrl/kraul/pelnyKraul.mp4"),
            VideoItem("kraul_nogi_deska", "Nogi kraul z deską", "$baseUrl/kraul/nogiKraulZDeska.mp4"),
            VideoItem("kraul_nogi_bez_deski", "Nogi kraul bez deski", "$baseUrl/kraul/nogiKraulBezDeski.mp4"),
            VideoItem("kraul_dokladanka_deska", "Dokładanka kraul z deską", "$baseUrl/kraul/dokladankaKraulDeskaUGory.mp4"),
            VideoItem("kraul_dokladanka_bez_deski", "Dokładanka kraul bez deski", "$baseUrl/kraul/dokladankaKraulBezDeski.mp4"),
            VideoItem("kraul_dokladanka_deska_w_nogach", "Dokładanka kraul z deską między nogami", "$baseUrl/kraul/dokladankaKraulZDeskaWNogach.mp4"),
            VideoItem("kraul_rece", "Ręce kraul", "$baseUrl/kraul/ramionaKraul.mp4")
        ),
        "Żaba" to listOf(
            VideoItem("zaba_pelna", "Żaba pełna", "$baseUrl/zaba/pelnaZaba.mp4"),
            VideoItem("zaba_nogi_deska", "Nogi żaba z deską", "$baseUrl/zaba/nogiZabaDeska.mp4"),
            VideoItem("zaba_nogi_bez_deski", "Nogi żaba bez deski", "$baseUrl/zaba/nogiZabaBezDeski.mp4"),
            VideoItem("zaba_piaty_styl", "Żaba i 5 styl", "$baseUrl/zaba/zabaZPiatymStylem.mp4")
        ),
        "Grzbiet" to listOf(
            VideoItem("grzbiet_pelny", "Grzbiet pełny", "$baseUrl/grzbiet/pelnyGrzbiet.mp4"),
            VideoItem("grzbiet_nogi", "Nogi grzbiet bez deski", "$baseUrl/grzbiet/nogiGrzbietTorpeda.mp4"),
            VideoItem("grzbiet_nogi_deska_gora", "Nogi grzbiet z deską u góry", "$baseUrl/grzbiet/nogiGrzbietDeskaUGory.mp4"),
            VideoItem("grzbiet_nogi_deska_dol", "Nogi grzbiet z deska na dole", "$baseUrl/grzbiet/nogiGrzbietDeskaNaDole.mp4"),
            VideoItem("grzbiet_dokladanka", "Dokładanka do grzbietu", "$baseUrl/grzbiet/dokladankaGrzbietZStrzalka.mp4"),
            VideoItem("grzbiet_dokladanka_deska_dol", "Dokładanka do grzbietu z deską na dole", "$baseUrl/grzbiet/dokladankaGrzbietDeskaNaDole.mp4"),
            VideoItem("grzbiet_dokladanka_deska_gora", "Dokładanka do grzbietu z deską u góry", "$baseUrl/grzbiet/dokladankaGrzbietDeskaUGory.mp4"),
            VideoItem("grzbiet_rece", "Ręce grzbiet", "$baseUrl/grzbiet/Kopia ramiona_grzbiet.mp4"),
            VideoItem("grzbiet_pelny_piaty_sty", "Grzbiet i 5 styl", "$baseUrl/grzbiet/pelnyGrzbiet5Styl.mp4")
        ),
        "Delfin" to listOf(
            VideoItem("delfin_pelny", "Delfin pełny", "$baseUrl/delfin/pelnyDelfin.mp4"),
            VideoItem("delfin_nogi_deska", "Nogi delfin z deską", "$baseUrl/delfin/nogiDelfinZDeska.mp4"),
            VideoItem("delfin_nogi_bez_deski", "Nogi delfin bez deski", "$baseUrl/delfin/nogiDelfinBezDeski.mp4"),
            VideoItem("delfin_rece", "Ręce delfin", "$baseUrl/delfin/ramionaDelfin.mp4")
        ),
        "Ćwiczenia" to listOf(
            VideoItem("cw_czucie_wody", "Czucie wody", "$baseUrl/cwiczenia/czucieWodyZDeska.mp4"),
            VideoItem("cw_glajch", "Glajch", "$baseUrl/cwiczenia/glajchWP.mp4"),
            VideoItem("cw_kraul_palce", "Ćwiczenie do kraula 1 (koniuszki palców)", "$baseUrl/cwiczenia/cwiczenieKoniuszkiPalcow.mp4"),
            VideoItem("cw_kraul_pod_woda", "Ćwiczenie do kraula 2 (kraul pod wodą)", "$baseUrl/cwiczenia/kraulPodWoda.mp4")
        )
    )

    var expandedCategories by remember { mutableStateOf(setOf<String>()) }
    var expandedVideos by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = Modifier.padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        videoCategories.forEach { (categoryName, variants) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expandedCategories =
                            if (categoryName in expandedCategories) {
                                expandedCategories - categoryName
                            } else {
                                expandedCategories + categoryName
                            }
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = categoryName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        color = Color.Black
                    )
                    Text(
                        text = if (categoryName in expandedCategories) "▲" else "▼",
                        fontSize = 18.sp,
                        color = Color(0xFF2196F3)
                    )
                }
            }

            if (categoryName in expandedCategories) {
                Spacer(modifier = Modifier.height(4.dp))
                variants.forEach { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedVideos =
                                        if (item.key in expandedVideos) {
                                            expandedVideos - item.key
                                        } else {
                                            expandedVideos + item.key
                                        }
                                },
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f),
                                    color = Color.Black
                                )
                                Text(
                                    text = if (item.key in expandedVideos) "▲" else "▶",
                                    fontSize = 18.sp,
                                    color = Color(0xFF2196F3)
                                )
                            }
                        }

                        AnimatedVisibility(visible = item.key in expandedVideos) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = item.title,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    NetworkVideoPlayer(
                                        videoUrl = item.url,
                                        title = item.title,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TrainingHistorySection(
    historyTrainings: List<Training>,
    expandedHistoryTrainings: MutableMap<String, Boolean>
) {
    if (historyTrainings.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "📭", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Brak historii treningów",
                    color = Color(0xFF999999),
                    fontSize = 16.sp
                )
            }
        }
    } else {
        historyTrainings.forEach { training ->
            val expanded = expandedHistoryTrainings[training.id] ?: false
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedHistoryTrainings[training.id] = !expanded }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = training.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (expanded) "▲" else "▼",
                        color = Color(0xFF2196F3),
                        fontSize = 18.sp
                    )
                }

                if (training.creationDate.isNotBlank()) {
                    Text(
                        text = "🗓️ Utworzono: ${formatDate(training.creationDate)}",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }

                if (!training.completedDate.isNullOrBlank()) {
                    Text(
                        text = "✅ Ukończono: ${formatDate(training.completedDate)}",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }

                Text(
                    text = "⭐ Ocena: ${training.rating ?: 0}/5",
                    fontSize = 12.sp,
                    color = Color(0xFF999999)
                )

                if (!training.note.isNullOrBlank()) {
                    Text(
                        text = "📝 Notatka: ${training.note}",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }

                AnimatedVisibility(visible = expanded) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        training.days.forEachIndexed { dayIdx, day ->
                            Text(
                                text = "Dzień ${dayIdx + 1}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3),
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                            day.tasks.sortedBy { it.order }.forEach { task ->
                                Text(
                                    text = "${task.order}. ${task.name}",
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                                Text(
                                    text = "Opis: ${task.description}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF999999),
                                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        if (dateString.isBlank()) return ""
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = input.parse(dateString)
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            .format(date ?: return dateString)
    } catch (e: Exception) {
        dateString
    }
}
