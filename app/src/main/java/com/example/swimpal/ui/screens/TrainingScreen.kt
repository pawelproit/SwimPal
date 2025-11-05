package com.example.swimpal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swimpal.viewmodel.TrainingViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import java.text.SimpleDateFormat
import java.util.*
import com.example.swimpal.model.Training

@Composable
fun TrainingScreen(
    trainingViewModel: TrainingViewModel = viewModel()
) {
    val customTrainings by trainingViewModel.customTrainings.collectAsState()
    val generatedTrainings by trainingViewModel.generatedTrainings.collectAsState()
    var errorMsg by remember { mutableStateOf("") }

    val expandedTrainings = remember { mutableStateMapOf<String, Boolean>() }
    val expandedDays = remember { mutableStateMapOf<String, Boolean>() }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var trainingToDelete by remember { mutableStateOf<Pair<String, String>?>(null) }

    var showCompleteDialog by remember { mutableStateOf(false) }
    var trainingToComplete by remember { mutableStateOf<Pair<Training, String>?>(null) }
    var completeRating by remember { mutableStateOf(3) }
    var completeNote by remember { mutableStateOf("") }
    var isCompleting by remember { mutableStateOf(false) }

    val globalOpis = """"🟦 Kraul (styl dowolny)

    Najpopularniejszy ze stylów pływackich. Pływamy na brzuchu, pracując jednocześnie nogami oraz naprzemiennie ramionami. Oddech bierzemy co kilka ruchów, przekręcając głowę na bok.

    Ćwiczenia techniczne:

    Ramiona kraul – mała deska między nogami (między kolanami a biodrami), pracujemy tylko ramionami, utrzymując napięcie mięśni brzucha.

    Nogi kraul – duża deska trzymana za oba końce, ręce proste, biodra wysoko, pracujemy samymi nogami, głowa pod wodą, oddech cykliczny.

    Dokładanka do kraula – duża deska z przodu, raz pracuje lewa, a raz prawa ręka, przy jednocześnie mocnej pracy nóg.

    🟩 Grzbiet (styl grzbietowy)

    Styl pływany na plecach. Ciało ułożone w jednej linii, biodra wysoko przy powierzchni wody. Ruchy ramion naprzemienne, nogi pracują ruchem zbliżonym do kraula, ale w odwrotnym ułożeniu. Oddech swobodny, twarz cały czas nad wodą.

    Ćwiczenia techniczne:

    Ramiona grzbiet – mała deska między nogami, pracują same ramiona, kontrola ruchu i rotacji tułowia.

    Nogi grzbiet – duża deska nad głową, ręce wyprostowane, praca samymi nogami, biodra wysoko.

    Dokładanka do grzbietu – jedna ręka cały czas wyprostowana, druga wykonuje ruch, zmiana co kilka cykli.

    🟨 Styl klasyczny (żabka)

    Styl pływany na brzuchu, gdzie ruch ramion i nóg wykonywany jest symetrycznie. Ręce pracują ruchem sercowym, nogi szerokim ruchem odpychającym. Głowa wychodzi z wody przy oddechu, a potem z powrotem się zanurza.

    Ćwiczenia techniczne:

    Ramiona żabka – mała deska między nogami, praca tylko rękami, skupienie na chwytaniu i prowadzeniu wody.

    Nogi żabka – duża deska trzymana przed sobą, praca samymi nogami, głowa w wodzie, oddech co kilka cykli.

    Dokładanka do żabki – jedna praca rąk na jeden cykl nóg, skupienie na koordynacji i rytmie.

    🟥 Styl zmienny (indywidualny)

    Styl łączący cztery techniki: delfin, grzbiet, żabka i kraul. Każdy odcinek pływamy innym stylem w tej kolejności. Wymaga dobrej techniki, kondycji i płynnych przejść między stylami.

    Ćwiczenia techniczne:

    Zmiany stylów – ćwiczenie przejść między stylami (np. delfin → grzbiet, grzbiet → żabka, żabka → kraul).

    Krótkie odcinki 4×25 m – każdy odcinek innym stylem, praca nad rytmem i utrzymaniem techniki.

    Zmienny na nogi – wszystkie style pływane tylko nogami z deską."""

    fun formatDate(dateString: String): String {
        return try {
            if (dateString.isBlank()) return ""
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = input.parse(dateString)
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date ?: return dateString)
        } catch (e: Exception) {
            dateString
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Ekran Treningów", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { expandedTrainings["opis"] = !(expandedTrainings["opis"] ?: false) }
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Opis zadań",
                    style = MaterialTheme.typography.titleMedium
                )
                AnimatedVisibility(
                    visible = expandedTrainings["opis"] ?: false,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Text(
                        text = globalOpis,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Text("Treningi własne", style = MaterialTheme.typography.titleMedium)
        if (customTrainings.isEmpty()) {
            Text("Brak własnych treningów")
        } else {
            customTrainings.forEach { training ->
                val expanded = expandedTrainings[training.id] ?: false
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedTrainings[training.id] = !expanded }
                                .padding(end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val date = formatDate(training.creationDate)
                            Text(
                                text = "${training.name}" + if(date.isNotBlank()) ", $date" else "",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    trainingToDelete = Pair(training.id, "custom_trainings")
                                    showDeleteDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Usuń trening")
                            }
                        }
                        AnimatedVisibility(
                            visible = expanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column {
                                training.days.forEachIndexed { idx, day ->
                                    val dayKey = "${training.id}_$idx"
                                    val expandedDay = expandedDays[dayKey] ?: false
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { expandedDays[dayKey] = !expandedDay }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Dzień ${idx + 1}",
                                            style = MaterialTheme.typography.labelLarge,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = if (expandedDay) "▲" else "▼",
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                    AnimatedVisibility(
                                        visible = expandedDay,
                                        enter = expandVertically(),
                                        exit = shrinkVertically()
                                    ) {
                                        Column(modifier = Modifier.padding(start = 16.dp)) {
                                            day.tasks.sortedBy { it.order }.forEach { task ->
                                                Text("${task.order}. ${task.name}", style = MaterialTheme.typography.labelSmall)
                                                Text("Opis: ${task.description}", style = MaterialTheme.typography.bodySmall)
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))


                                Button(
                                    onClick = {
                                        trainingToComplete = Pair(training, "custom_trainings")
                                        completeRating = 3
                                        completeNote = ""
                                        showCompleteDialog = true
                                    },
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Text("Oznacz jako wykonane")
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
//1/2

        Text("Treningi generowane", style = MaterialTheme.typography.titleMedium)
        if (generatedTrainings.isEmpty()) {
            Text("Brak generowanych treningów")
        } else {
            generatedTrainings.forEach { training ->
                val expanded = expandedTrainings[training.id] ?: false
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedTrainings[training.id] = !expanded }
                                .padding(end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val date = formatDate(training.creationDate)
                            Text(
                                text = "${training.name}" + if(date.isNotBlank()) ", $date" else "",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    trainingToDelete = Pair(training.id, "generated_trainings")
                                    showDeleteDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Usuń trening")
                            }
                        }
                        AnimatedVisibility(
                            visible = expanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column {
                                training.days.forEachIndexed { idx, day ->
                                    val dayKey = "${training.id}_$idx"
                                    val expandedDay = expandedDays[dayKey] ?: false
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { expandedDays[dayKey] = !expandedDay }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Dzień ${idx + 1}",
                                            style = MaterialTheme.typography.labelLarge,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = if (expandedDay) "▲" else "▼",
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                    AnimatedVisibility(
                                        visible = expandedDay,
                                        enter = expandVertically(),
                                        exit = shrinkVertically()
                                    ) {
                                        Column(modifier = Modifier.padding(start = 16.dp)) {
                                            day.tasks.sortedBy { it.order }.forEach { task ->
                                                Text("${task.order}. ${task.name}", style = MaterialTheme.typography.labelSmall)
                                                Text("Opis: ${task.description}", style = MaterialTheme.typography.bodySmall)
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        trainingToComplete = Pair(training, "generated_trainings")
                                        completeRating = 3
                                        completeNote = ""
                                        showCompleteDialog = true
                                    },
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Text("Oznacz jako wykonane")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (errorMsg.isNotEmpty()) {
            Text(errorMsg, color = MaterialTheme.colorScheme.error)
        }
    }


    if (showDeleteDialog && trainingToDelete != null) {
        val currentTrainingId = trainingToDelete!!.first
        val currentCollection = trainingToDelete!!.second

        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                trainingToDelete = null
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        trainingToDelete = null


                        trainingViewModel.deleteTraining(
                            currentTrainingId,
                            currentCollection,
                            onSuccess = {
                            },
                            onError = { e ->
                                errorMsg = "Błąd podczas usuwania treningu: ${e.message}"
                            }
                        )
                    }
                ) { Text("Tak") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        trainingToDelete = null
                    }
                ) { Text("Nie") }
            },
            title = { Text("Usuń trening") },
            text = { Text("Czy na pewno chcesz usunąć ten trening?") }
        )
    }


    if (showCompleteDialog && trainingToComplete != null) {
        val currentTraining = trainingToComplete!!.first
        val currentCollection = trainingToComplete!!.second
        val currentRating = completeRating
        val currentNote = completeNote

        AlertDialog(
            onDismissRequest = {
                if (!isCompleting) {
                    showCompleteDialog = false
                    trainingToComplete = null
                }
            },
            title = { Text("Ocena treningu") },
            text = {
                Column {
                    Text("Jak oceniasz ten trening?")
                    Row(
                        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                    ) {
                        (1..5).forEach { value ->
                            Button(
                                onClick = { completeRating = value },
                                colors = if (completeRating == value) {
                                    ButtonDefaults.buttonColors()
                                } else {
                                    ButtonDefaults.outlinedButtonColors()
                                },
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Text("$value")
                            }
                        }
                    }
                    OutlinedTextField(
                        value = completeNote,
                        onValueChange = { completeNote = it },
                        label = { Text("Notatka (opcjonalnie)") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isCompleting
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isCompleting = true

                        trainingViewModel.completeTraining(
                            training = currentTraining,
                            collectionName = currentCollection,
                            rating = completeRating,
                            note = completeNote,
                            onSuccess = {
                                isCompleting = false
                                showCompleteDialog = false
                                trainingToComplete = null
                                completeRating = 3
                                completeNote = ""
                            },
                            onError = { e ->
                                errorMsg = "Błąd przy zapisie oceny/notatki: ${e.message}"
                                isCompleting = false
                                showCompleteDialog = false
                                trainingToComplete = null
                            }
                        )
                    },
                    enabled = !isCompleting
                ) {
                    if (isCompleting) {
                        Text("Zapisywanie...")
                    } else {
                        Text("Zapisz")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        if (!isCompleting) {
                            showCompleteDialog = false
                            trainingToComplete = null
                        }
                    },
                    enabled = !isCompleting
                ) { Text("Anuluj") }
            }
        )
    }

}
