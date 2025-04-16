package com.example.swimpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.swimpal.ui.theme.SwimPalTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.compose.ui.unit.dp



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserFlowApp()
        }
    }
}

@Composable
fun UserFlowApp() {
    val navController = rememberNavController()
    val viewModel: UserInputViewModel = viewModel()

    NavHost(navController = navController, startDestination = "age") {
        composable("age") { AgeScreen(navController, viewModel) }
        composable("gender") { GenderScreen(navController, viewModel) }
        composable("weight") { WeightScreen(navController, viewModel) }
        composable("height") { HeightScreen(navController, viewModel) }
        composable("fitness") { FitnessLevelScreen(navController, viewModel) }
        composable("goal") { GoalScreen(navController, viewModel) }
        composable("days") { TrainingDaysScreen(navController, viewModel) }
        composable("main") { MainScreen(viewModel) }
    }
}

class UserInputViewModel : ViewModel() {
    var age by mutableStateOf(0)
    var gender by mutableStateOf("")
    var weight by mutableStateOf(0)
    var height by mutableStateOf(0)
    var fitnessLevel by mutableStateOf(1)
    var goal by mutableStateOf("")
    var trainingDays by mutableStateOf(1)
}

@Composable
fun AgeScreen(navController: NavController, viewModel: UserInputViewModel) {
    var age by remember { mutableStateOf(viewModel.age) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center) {
        Text("Podaj swój wiek (0–100)")
        Slider(
            value = age.toFloat(),
            onValueChange = { age = it.toInt() },
            valueRange = 0f..100f
        )
        Text("Wiek: $age lat")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.age = age
            navController.navigate("gender")
        }) {
            Text("Dalej")
        }
    }
}

@Composable
fun GenderScreen(navController: NavController, viewModel: UserInputViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Wybierz swoją płeć:")

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                viewModel.gender = "kobieta"
                navController.navigate("weight")
            }) {
                Text("Kobieta")
            }
            Button(onClick = {
                viewModel.gender = "mężczyzna"
                navController.navigate("weight")
            }) {
                Text("Mężczyzna")
            }
        }
    }
}



@Composable
fun WeightScreen(navController: NavController, viewModel: UserInputViewModel) {
    var weight by remember { mutableStateOf(viewModel.weight) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center) {
        Text("Podaj swoją wagę TEST222222 (0–200 kg)")
        Slider(
            value = weight.toFloat(),
            onValueChange = { weight = it.toInt() },
            valueRange = 0f..200f
        )
        Text("Waga: $weight kg")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.weight = weight
            navController.navigate("height")
        }) {
            Text("Dalej")
        }
    }
}


@Composable
fun FitnessLevelScreen(navController: NavController, viewModel: UserInputViewModel) {
    var level by remember { mutableStateOf(viewModel.fitnessLevel) }

    val descriptions = listOf(
        "1 – Początkujący",
        "2 – Słaba kondycja",
        "3 – Średnia forma",
        "4 – Dobra forma",
        "5 – Bardzo zaawansowany"
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center) {
        Text("Wybierz poziom wytrenowania:")
        Slider(
            value = level.toFloat(),
            onValueChange = { level = it.toInt().coerceIn(1, 5) },
            valueRange = 1f..5f,
            steps = 3
        )
        Text(descriptions[level - 1])
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.fitnessLevel = level
            navController.navigate("goal")
        }) {
            Text("Dalej")
        }
    }
}




@Composable
fun HeightScreen(navController: NavController, viewModel: UserInputViewModel) {
    var height by remember { mutableStateOf(viewModel.height) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center) {
        Text("Podaj swój wzrost (0–250 cm)")
        Slider(
            value = height.toFloat(),
            onValueChange = { height = it.toInt() },
            valueRange = 0f..250f
        )
        Text("Wzrost: $height cm")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.height = height
            navController.navigate("fitness")
        }) {
            Text("Dalej")
        }
    }
}


@Composable
fun GoalScreen(navController: NavController, viewModel: UserInputViewModel) {
    val goals = listOf("Sprinty", "Triathlon", "Open water", "Zdrowy kręgosłup", "Technika")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center) {
        Text("Wybierz swój cel treningowy:")

        goals.forEach { goal ->
            Button(
                onClick = {
                    viewModel.goal = goal
                    navController.navigate("days")
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(goal)
            }
        }
    }
}



@Composable
fun TrainingDaysScreen(navController: NavController, viewModel: UserInputViewModel) {
    var days by remember { mutableStateOf(viewModel.trainingDays) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center) {
        Text("Ile dni w tygodniu możesz trenować? (1–5)")
        Slider(
            value = days.toFloat(),
            onValueChange = { days = it.toInt().coerceIn(1, 5) },
            valueRange = 1f..5f,
            steps = 3
        )
        Text("Treningów w tygodniu: $days")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.trainingDays = days
            navController.navigate("main")
        }) {
            Text("Zakończ")
        }
    }
}

@Composable
fun MainScreen(viewModel: UserInputViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center) {
        Text("Witaj w aplikacji!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Twoje dane:")
        Text("Wiek: ${viewModel.age}")
        Text("Płeć: ${viewModel.gender}")
        Text("Waga: ${viewModel.weight} kg")
        Text("Wzrost: ${viewModel.height} cm")
        Text("Poziom: ${viewModel.fitnessLevel}")
        Text("Cel: ${viewModel.goal}")
        Text("Dni treningowe: ${viewModel.trainingDays}")
    }
}

