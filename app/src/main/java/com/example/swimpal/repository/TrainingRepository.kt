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
 * Repository responsible for managing user's trainings stored in Firestore.
 *
 * It exposes separate reactive streams for custom and generated trainings and
 * provides operations for saving, deleting and reading template trainings.
 */
class TrainingRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val user get() = auth.currentUser

    private val _customTrainings = MutableStateFlow<List<Training>>(emptyList())

    /**
     * Public read‑only stream of custom trainings created by the user.
     */
    val customTrainings: StateFlow<List<Training>> = _customTrainings

    private val _generatedTrainings = MutableStateFlow<List<Training>>(emptyList())

    /**
     * Public read‑only stream of trainings generated automatically for the user.
     */
    val generatedTrainings: StateFlow<List<Training>> = _generatedTrainings

    /**
     * Converts a [Training] object into a map representation suitable for Firestore.
     *
     * Non‑empty optional fields such as type, completedDate, rating and note
     * are added only when present in the model.
     *
     * @param training Training instance to be serialized.
     * @return Map that can be directly written to Firestore.
     */
    private fun toMap(training: Training): Map<String, Any> = buildMap {
        put("name", training.name)
        put("days", training.days.map { day ->
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
        })
        put("creationDate", training.creationDate)
        if (training.type.isNotEmpty()) {
            put("type", training.type)
        }
        training.completedDate?.let { put("completedDate", it) }
        training.rating?.let { put("rating", it) }
        training.note?.let { put("note", it) }
    }

    /**
     * Subscribes to the user's custom trainings in Firestore and updates [customTrainings].
     *
     * If the user is not logged in or an error occurs, the local list is set to empty.
     */
    fun fetchCustomTrainings() {
        val u = user ?: run {
            _customTrainings.value = emptyList()
            return
        }

        db.collection("users")
            .document(u.uid)
            .collection("custom_trainings")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    _customTrainings.value = emptyList()
                    return@addSnapshotListener
                }
                _customTrainings.value = snapshot.documents.mapNotNull {
                    parseTraining(it.data, it.id)
                }
            }
    }

    /**
     * Subscribes to the user's generated trainings in Firestore and updates [generatedTrainings].
     *
     * If the user is not logged in or an error occurs, the local list is set to empty.
     */
    fun fetchGeneratedTrainings() {
        val u = user ?: run {
            _generatedTrainings.value = emptyList()
            return
        }

        db.collection("users")
            .document(u.uid)
            .collection("generated_trainings")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    _generatedTrainings.value = emptyList()
                    return@addSnapshotListener
                }
                _generatedTrainings.value = snapshot.documents.mapNotNull {
                    parseTraining(it.data, it.id)
                }
            }
    }

    /**
     * Saves a new training in the given user subcollection.
     *
     * A [Training] object is created from the provided parameters, enriched with
     * the current date as creation date, and written to Firestore.
     *
     * @param trainingName Name of the training plan.
     * @param type Type of training (for example "Custom", "Sprint", "Open Water").
     * @param days List of training days with their tasks.
     * @param collection Name of the user subcollection in which to store the training.
     * @param onSuccess Callback invoked when the training is saved successfully.
     * @param onError Callback invoked when the user is not logged in or the write fails.
     */
    fun saveTraining(
        trainingName: String,
        type: String,
        days: List<TrainingDay>,
        collection: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val u = user ?: run {
            onError(Exception("Nie zalogowano użytkownika"))
            return
        }

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val training = Training(
            name = trainingName,
            type = type,
            days = days,
            creationDate = today
        )

        db.collection("users")
            .document(u.uid)
            .collection(collection)
            .add(toMap(training))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    /**
     * Deletes a training document from the specified user subcollection.
     *
     * @param trainingId Identifier of the training document to delete.
     * @param collection Name of the user subcollection (for example "custom_trainings").
     * @param onSuccess Callback invoked when the document is removed successfully.
     * @param onError Callback invoked when the user is not logged in or the deletion fails.
     */
    fun deleteTraining(
        trainingId: String,
        collection: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val u = user ?: run {
            onError(Exception("Nie zalogowano użytkownika"))
            return
        }

        db.collection("users")
            .document(u.uid)
            .collection(collection)
            .document(trainingId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    /**
     * Fetches template training days from a public collection used for generating plans.
     *
     * Documents are limited by [days], mapped to [TrainingDay] and passed through [onSuccess].
     * Typical usage is to read predefined exercises and convert them into a generated plan.
     *
     * @param collectionName Name of the Firestore collection with template days.
     * @param days Maximum number of documents (days) to load.
     * @param onSuccess Callback receiving the list of mapped [TrainingDay] objects.
     * @param onError Callback invoked when the read operation fails.
     */
    fun fetchTemplateTraining(
        collectionName: String,
        days: Int,
        onSuccess: (List<TrainingDay>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(collectionName)
            .limit(days.toLong())
            .get()
            .addOnSuccessListener { snapshot ->
                val trainingDays = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    val exercises = (data["exercises"] as? List<*>)?.mapNotNull { e ->
                        (e as? Map<*, *>)?.let { taskObj ->
                            TrainingTask(
                                name = taskObj["nazwa"] as? String ?: "",
                                description = taskObj["opis"] as? String ?: "",
                                order = (taskObj["order"] as? Long)?.toInt() ?: 0
                            )
                        }
                    } ?: emptyList()
                    TrainingDay(
                        dayName = data["dayName"] as? String ?: doc.id,
                        tasks = exercises
                    )
                }
                onSuccess(trainingDays)
            }
            .addOnFailureListener { e -> onError(e) }
    }

    /**
     * Parses a Firestore document map into a [Training] domain object.
     *
     * @param map Raw map of values coming from Firestore.
     * @param id Identifier of the Firestore document.
     * @return Parsed [Training] instance or null when input data is invalid.
     */
    private fun parseTraining(map: Map<String, Any>?, id: String): Training? {
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
            type = map["type"] as? String ?: "",
            days = days,
            creationDate = map["creationDate"] as? String ?: "",
            completedDate = map["completedDate"] as? String?,
            rating = (map["rating"] as? Long)?.toInt(),
            note = map["note"] as? String
        )
    }
}
