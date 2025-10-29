package com.example.swimpal.viewmodel

import androidx.lifecycle.ViewModel
import com.example.swimpal.model.Training
import com.example.swimpal.model.TrainingDay
import com.example.swimpal.model.TrainingTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

class TrainingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val user get() = FirebaseAuth.getInstance().currentUser

    private val _customTrainings = MutableStateFlow<List<Training>>(emptyList())
    val customTrainings: StateFlow<List<Training>> = _customTrainings

    private val _generatedTrainings = MutableStateFlow<List<Training>>(emptyList())
    val generatedTrainings: StateFlow<List<Training>> = _generatedTrainings

    private val _historyTrainings = MutableStateFlow<List<Training>>(emptyList())
    val historyTrainings: StateFlow<List<Training>> = _historyTrainings

    init {
        fetchCustomTrainings()
        fetchGeneratedTrainings()
        fetchHistoryTrainings()
    }

    private fun toMap(training: Training): Map<String, Any> = mapOf(
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
        "creationDate" to training.creationDate
    )

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

    fun generateAndSaveTraining(
        type: String,
        difficulty: Int,
        days: Int,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val collectionName = when (type) {
            "Sprinty" -> "sprints_level_$difficulty"
            "Triathlon" -> "triathlon_level_$difficulty"
            "Open Water" -> "open_water_level_$difficulty"
            "Technika" -> "technique_level_$difficulty"
            else -> "sprints_level_1"
        }

        fetchTemplateTraining(
            collectionName = collectionName,
            days = days,
            onSuccess = { trainingDays ->
                saveGeneratedTraining(
                    trainingName = "$type – poziom $difficulty ($days dni)",
                    days = trainingDays,
                    onSuccess = onSuccess,
                    onError = onError
                )
            },
            onError = onError
        )
    }

    private fun incrementCustomCountAndDays(onComplete: () -> Unit) {
        val u = user ?: return
        val userDoc = db.collection("users").document(u.uid)
        userDoc.get().addOnSuccessListener { doc ->
            val profile = doc.data
            val customCount = ((profile?.get("customCount") as? Long) ?: 0L) + 1
            val generatedCount = (profile?.get("generatedCount") as? Long ?: 0L)
            val totalCount = customCount + generatedCount

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val trainingDates = ((profile?.get("trainingDates") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()).toMutableSet()
            trainingDates.add(today)
            val activeDays = trainingDates.size

            userDoc.update(
                mapOf(
                    "customCount" to customCount,
                    "totalCount" to totalCount,
                    "activeDays" to activeDays,
                    "trainingDates" to trainingDates.toList()
                )
            ).addOnSuccessListener { onComplete() }
        }
    }

    private fun incrementGeneratedCountAndDays(onComplete: () -> Unit) {
        val u = user ?: return
        val userDoc = db.collection("users").document(u.uid)
        userDoc.get().addOnSuccessListener { doc ->
            val profile = doc.data
            val generatedCount = ((profile?.get("generatedCount") as? Long) ?: 0L) + 1
            val customCount = (profile?.get("customCount") as? Long ?: 0L)
            val totalCount = customCount + generatedCount

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val trainingDates = ((profile?.get("trainingDates") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()).toMutableSet()
            trainingDates.add(today)
            val activeDays = trainingDates.size

            userDoc.update(
                mapOf(
                    "generatedCount" to generatedCount,
                    "totalCount" to totalCount,
                    "activeDays" to activeDays,
                    "trainingDates" to trainingDates.toList()
                )
            ).addOnSuccessListener { onComplete() }
        }
    }

    fun saveCustomTraining(
        trainingName: String,
        days: List<TrainingDay>,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val u = user
        if (u == null) {
            onError(Exception("Nie zalogowano użytkownika"))
            return
        }
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val training = Training(name = trainingName, days = days, creationDate = today)
        db.collection("users")
            .document(u.uid)
            .collection("custom_trainings")
            .add(toMap(training))
            .addOnSuccessListener {
                incrementCustomCountAndDays(onSuccess)
            }
            .addOnFailureListener { e -> onError(e) }
    }

    fun saveGeneratedTraining(
        trainingName: String,
        days: List<TrainingDay>,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val u = user
        if (u == null) {
            onError(Exception("Nie zalogowano użytkownika"))
            return
        }
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val training = Training(name = trainingName, days = days, creationDate = today)
        db.collection("users")
            .document(u.uid)
            .collection("generated_trainings")
            .add(toMap(training))
            .addOnSuccessListener {
                incrementGeneratedCountAndDays(onSuccess)
            }
            .addOnFailureListener { e -> onError(e) }
    }

    fun fetchCustomTrainings() {
        user?.let { u ->
            db.collection("users")
                .document(u.uid)
                .collection("custom_trainings")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        _customTrainings.value = emptyList()
                        return@addSnapshotListener
                    }
                    val trainings = snapshot.documents.mapNotNull { doc ->
                        val map = doc.data ?: return@mapNotNull null
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
                        Training(
                            id = doc.id,
                            name = map["name"] as? String ?: "",
                            days = days,
                            creationDate = map["creationDate"] as? String ?: ""
                        )
                    }
                    _customTrainings.value = trainings
                }
        }
    }

    fun fetchGeneratedTrainings() {
        user?.let { u ->
            db.collection("users")
                .document(u.uid)
                .collection("generated_trainings")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        _generatedTrainings.value = emptyList()
                        return@addSnapshotListener
                    }
                    val trainings = snapshot.documents.mapNotNull { doc ->
                        val map = doc.data ?: return@mapNotNull null
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
                        Training(
                            id = doc.id,
                            name = map["name"] as? String ?: "",
                            days = days,
                            creationDate = map["creationDate"] as? String ?: ""
                        )
                    }
                    _generatedTrainings.value = trainings
                }
        }
    }

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
                    val trainings = snapshot.documents.mapNotNull { doc ->
                        val map = doc.data ?: return@mapNotNull null
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
                        Training(
                            id = doc.id,
                            name = map["name"] as? String ?: "",
                            days = days,
                            creationDate = map["creationDate"] as? String ?: "",
                            completedDate = map["completedDate"] as? String ?: "",
                            rating = (map["rating"] as? Long)?.toInt() ?: 0,
                            note = map["note"] as? String ?: ""
                        )
                    }
                    _historyTrainings.value = trainings
                }
        }
    }

    fun deleteTraining(trainingId: String, collection: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val u = user ?: return
        db.collection("users")
            .document(u.uid)
            .collection(collection)
            .document(trainingId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    fun completeTraining(
        training: Training,
        rating: Int,
        note: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val u = user ?: return onError(Exception("Nie zalogowano użytkownika"))
        val historyData = training.copy(
            completedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            rating = rating,
            note = note
        )
        val historyMap = toHistoryMap(historyData)
        db.collection("users")
            .document(u.uid)
            .collection("history_trainings")
            .add(historyMap)
            .addOnSuccessListener {
                val collectionName = if (_customTrainings.value.any { it.id == training.id }) "custom_trainings" else "generated_trainings"
                deleteTraining(training.id, collectionName, onSuccess, onError)
            }
            .addOnFailureListener { e -> onError(e) }
    }
}
