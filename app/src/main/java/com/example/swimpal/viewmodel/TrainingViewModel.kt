package com.example.swimpal.viewmodel

import androidx.lifecycle.ViewModel
import com.example.swimpal.model.Training
import com.example.swimpal.model.TrainingDay
import com.example.swimpal.repository.HistoryRepository
import com.example.swimpal.repository.TrainingRepository
import com.example.swimpal.repository.UserStatsRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel responsible for managing trainings lifecycle.
 *
 * Coordinates operations related to generated trainings, custom trainings
 * and completed training history. Acts as an orchestration layer between
 * UI and repositories.
 *
 * Responsibilities:
 * - Exposes streams of generated, custom and historical trainings.
 * - Generates trainings from predefined templates.
 * - Saves and deletes trainings.
 * - Marks trainings as completed and moves them to history.
 * - Updates user statistics after successful operations.
 */
class TrainingViewModel : ViewModel() {

    private val trainingRepo = TrainingRepository()
    private val historyRepo = HistoryRepository()
    private val statsRepo = UserStatsRepository()

    /**
     * Stream of user-created custom trainings.
     */
    val customTrainings: StateFlow<List<Training>> = trainingRepo.customTrainings

    /**
     * Stream of automatically generated trainings.
     */
    val generatedTrainings: StateFlow<List<Training>> = trainingRepo.generatedTrainings

    /**
     * Stream of completed trainings stored in history.
     */
    val historyTrainings: StateFlow<List<Training>> = historyRepo.historyTrainings

    init {
        trainingRepo.fetchCustomTrainings()
        trainingRepo.fetchGeneratedTrainings()
        historyRepo.fetchHistoryTrainings()
    }

    /**
     * Generates a training plan based on provided parameters and saves it.
     *
     * Selects a template collection based on training type and difficulty,
     * limits the number of days, saves the generated training, and updates
     * user statistics on success.
     *
     * @param type Training type (e.g. Sprinty, Triathlon).
     * @param difficulty Difficulty level (1–3).
     * @param days Number of training days.
     * @param onSuccess Callback invoked after successful generation and save.
     * @param onError Callback invoked when any step of the process fails.
     */
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

    /**
     * Saves a manually created custom training.
     *
     * Persists the training, then increments user statistics related to
     * custom trainings.
     *
     * @param trainingName Display name of the training.
     * @param days List of training days with tasks.
     * @param onSuccess Callback invoked after successful save.
     * @param onError Callback invoked when saving or stats update fails.
     */
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

    /**
     * Deletes a training from the specified collection.
     *
     * @param trainingId Identifier of the training to delete.
     * @param collection Firestore collection name.
     * @param onSuccess Callback invoked after successful deletion.
     * @param onError Callback invoked when deletion fails.
     */
    fun deleteTraining(
        trainingId: String,
        collection: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        trainingRepo.deleteTraining(trainingId, collection, onSuccess, onError)
    }

    /**
     * Marks a training as completed and moves it to history.
     *
     * Adds the training to history with rating and note, then removes it
     * from the active trainings collection.
     *
     * @param training Training being completed.
     * @param collectionName Source collection of the training.
     * @param rating User rating of the training.
     * @param note Optional user note.
     * @param onSuccess Callback invoked after successful completion.
     * @param onError Callback invoked when the operation fails.
     */
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
