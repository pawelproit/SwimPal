package com.example.swimpal.viewmodel

import androidx.lifecycle.ViewModel
import com.example.swimpal.model.Training
import com.example.swimpal.model.TrainingDay
import com.example.swimpal.repository.HistoryRepository
import com.example.swimpal.repository.TrainingRepository
import com.example.swimpal.repository.UserStatsRepository
import kotlinx.coroutines.flow.StateFlow

class TrainingViewModel : ViewModel() {

    private val trainingRepo = TrainingRepository()
    private val historyRepo = HistoryRepository()
    private val statsRepo = UserStatsRepository()

    val customTrainings: StateFlow<List<Training>> = trainingRepo.customTrainings
    val generatedTrainings: StateFlow<List<Training>> = trainingRepo.generatedTrainings
    val historyTrainings: StateFlow<List<Training>> = historyRepo.historyTrainings

    init {
        trainingRepo.fetchCustomTrainings()
        trainingRepo.fetchGeneratedTrainings()
        historyRepo.fetchHistoryTrainings()
    }

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

        trainingRepo.fetchTemplateTraining(
            collectionName = collectionName,
            days = days,
            onSuccess = { trainingDays ->
                trainingRepo.saveTraining(
                    trainingName = "$type – poziom $difficulty ($days dni)",
                    type = type,
                    days = trainingDays,
                    collection = "generated_trainings",
                    onSuccess = {
                        statsRepo.incrementCount(
                            isCustom = false,
                            onSuccess = onSuccess,
                            onError = onError
                        )
                    },
                    onError = onError
                )
            },
            onError = onError
        )
    }

    fun saveCustomTraining(
        trainingName: String,
        days: List<TrainingDay>,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        trainingRepo.saveTraining(
            trainingName = trainingName,
            type = "custom",
            days = days,
            collection = "custom_trainings",
            onSuccess = {
                statsRepo.incrementCount(
                    isCustom = true,
                    onSuccess = onSuccess,
                    onError = onError
                )
            },
            onError = onError
        )
    }

    fun deleteTraining(
        trainingId: String,
        collection: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        trainingRepo.deleteTraining(trainingId, collection, onSuccess, onError)
    }

    fun completeTraining(
        training: Training,
        collectionName: String,
        rating: Int,
        note: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        historyRepo.addToHistory(
            training = training,
            rating = rating,
            note = note,
            onSuccess = {
                trainingRepo.deleteTraining(
                    trainingId = training.id,
                    collection = collectionName,
                    onSuccess = onSuccess,
                    onError = onError
                )
            },
            onError = onError
        )
    }
}
