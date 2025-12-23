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

/**
 * Repository responsible for managing the user's training history stored in Firestore.
 *
 * It exposes a reactive stream of history trainings and provides helper methods to
 * add completed trainings to history and parse Firestore documents into domain models.
 */
class HistoryRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val user get() = auth.currentUser

    private val _historyTrainings = MutableStateFlow<List<Training>>(emptyList())

    /**
     * Public read-only stream of the user's training history.
     *
     * The list is automatically updated when Firestore emits new snapshots.
     */
    val historyTrainings: StateFlow<List<Training>> = _historyTrainings

    /**
     * Converts a [Training] object into a map representation suitable for Firestore.
     *
     * @param training Training instance that should be stored in the history collection.
     * @return Map with primitive and nested values that can be persisted in Firestore.
     */
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

    /**
     * Subscribes to the user's history trainings in Firestore and updates [historyTrainings].
     *
     * If the user is not logged in or an error occurs, the local history list is set to empty.
     */
    fun fetchHistoryTrainings() {
        val u = user ?: run {
            _historyTrainings.value = emptyList()
            return
        }

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

    /**
     * Adds a completed training to the user's history in Firestore.
     *
     * The method sets the completion date, rating and note, writes the data to
     * the `history_trainings` subcollection, refreshes local history on success,
     * and forwards success or error via callbacks.
     *
     * @param training Training that has been completed.
     * @param rating User rating for the completed training.
     * @param note Optional note entered by the user about this training.
     * @param onSuccess Callback invoked after a successful write and local refresh.
     * @param onError Callback invoked when the user is not logged in or the write fails.
     */
    fun addToHistory(
        training: Training,
        rating: Int,
        note: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val u = user ?: run {
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

    /**
     * Parses a Firestore document map into a [Training] history object.
     *
     * @param map Raw data map retrieved from Firestore.
     * @param id Identifier of the Firestore document.
     * @return Parsed [Training] instance or null if the map is invalid.
     */
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
            completedDate = map["completedDate"] as? String?,
            rating = (map["rating"] as? Long)?.toInt(),
            note = map["note"] as? String
        )
    }
}
