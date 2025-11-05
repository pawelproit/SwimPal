package com.example.swimpal.repository

import com.example.swimpal.model.Training
import com.example.swimpal.model.TrainingDay
import com.example.swimpal.model.TrainingTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

class HistoryRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val user get() = auth.currentUser

    private val _historyTrainings = MutableStateFlow<List<Training>>(emptyList())
    val historyTrainings: StateFlow<List<Training>> = _historyTrainings

    private fun toHistoryMap(training: Training): Map<String, Any> = mapOf(
        "name" to training.name,
        "days" to training.days.map { day ->
            mapOf(
                "dayName" to day.dayName,
                "tasks" to day.tasks.map { task ->
                    mapOf(
                        "name" to task.name,
                        "description" to task.description,
                        "order" to task.order
                    )
                }
            )
        },
        "creationDate" to training.creationDate,
        "completedDate" to (training.completedDate ?: ""),
        "rating" to (training.rating ?: 0),
        "note" to (training.note ?: "")
    )

    fun fetchHistoryTrainings() {
        user?.let { u ->
            db.collection("users")
                .document(u.uid)
                .collection("history_trainings")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        _historyTrainings.value = emptyList()
                        return@addSnapshotListener
                    }
                    _historyTrainings.value = snapshot.documents.mapNotNull {
                        parseHistoryTraining(it.data, it.id)
                    }
                }
        }
    }

    fun addToHistory(
        training: Training,
        rating: Int,
        note: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val u = user
        if (u == null) {
            onError(Exception("Nie zalogowano użytkownika"))
            return
        }

        val completedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val historyData = training.copy(
            completedDate = completedDate,
            rating = rating,
            note = note
        )
        val historyMap = toHistoryMap(historyData)

        db.collection("users")
            .document(u.uid)
            .collection("history_trainings")
            .add(historyMap)
            .addOnSuccessListener {
                fetchHistoryTrainings()
                onSuccess()
            }
            .addOnFailureListener { e -> onError(e) }
    }

    private fun parseHistoryTraining(map: Map<String, Any>?, id: String): Training? {
        if (map == null) return null
        val days = (map["days"] as? List<*>)?.mapNotNull { dayMap ->
            dayMap as? Map<*, *>
        }?.map { dayObj ->
            val tasksList = (dayObj["tasks"] as? List<*>)?.mapNotNull { taskMap ->
                taskMap as? Map<*, *>
            }?.map { taskObj ->
                TrainingTask(
                    name = taskObj["name"] as? String ?: "",
                    description = taskObj["description"] as? String ?: "",
                    order = (taskObj["order"] as? Long)?.toInt() ?: 0
                )
            } ?: emptyList()
            TrainingDay(
                dayName = dayObj["dayName"] as? String ?: "",
                tasks = tasksList
            )
        } ?: emptyList()

        return Training(
            id = id,
            name = map["name"] as? String ?: "",
            days = days,
            creationDate = map["creationDate"] as? String ?: "",
            completedDate = map["completedDate"] as? String ?: "",
            rating = (map["rating"] as? Long)?.toInt() ?: 0,
            note = map["note"] as? String ?: ""
        )
    }
}
