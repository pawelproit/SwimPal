package com.example.swimpal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swimpal.model.Training
import com.example.swimpal.model.TrainingTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrainingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val user get() = FirebaseAuth.getInstance().currentUser

    private val _trainings = MutableStateFlow<List<Training>>(emptyList())
    val trainings: StateFlow<List<Training>> = _trainings

    init {
        fetchTrainings()
    }

    fun saveCustomTraining(
        trainingName: String,
        tasks: List<TrainingTask>,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            onError(Exception("Nie zalogowano użytkownika"))
            return
        }
        val training = Training(name = trainingName, tasks = tasks)
        db.collection("users")
            .document(user.uid)
            .collection("trainings")
            .add(training)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    fun fetchTrainings() {
        user?.let {
            db.collection("users")
                .document(it.uid)
                .collection("trainings")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        _trainings.value = emptyList()
                        return@addSnapshotListener
                    }
                    val data = snapshot.documents.map { doc ->
                        val training = doc.toObject(Training::class.java)
                        training?.copy(id = doc.id)
                    }.filterNotNull()
                    _trainings.value = data
                }
        }
    }

    fun generateAndSaveTraining(
        type: String,
        difficulty: Int,
        days: Int,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        // Przykładowa prosta logika generowania - rozwiń wedle potrzeby!
        val tasks = List(days) { day ->
            TrainingTask(
                name = "Dzień ${day + 1} – $type – poziom $difficulty",
                description = "Opis zadań dla dnia ${day + 1} ($type, poziom $difficulty)"
            )
        }
        saveCustomTraining(
            trainingName = "$type – poziom $difficulty ($days dni)",
            tasks = tasks,
            onSuccess = onSuccess,
            onError = onError
        )
    }
}
