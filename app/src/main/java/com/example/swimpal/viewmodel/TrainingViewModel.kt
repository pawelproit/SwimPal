package com.example.swimpal.viewmodel

import androidx.lifecycle.ViewModel
import com.example.swimpal.model.Training
import com.example.swimpal.model.TrainingTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TrainingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

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
        val db = FirebaseFirestore.getInstance()
        val training = Training(name = trainingName, tasks = tasks)

        db.collection("users")
            .document(user.uid)
            .collection("trainings")
            .add(training)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    private fun fetchTrainings() {
        user?.let {
            db.collection("users")
                .document(it.uid)
                .collection("trainings")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        _trainings.value = emptyList()
                        return@addSnapshotListener
                    }
                    _trainings.value = snapshot.documents.mapNotNull { it.toObject(Training::class.java) }
                }
        }
    }
}
