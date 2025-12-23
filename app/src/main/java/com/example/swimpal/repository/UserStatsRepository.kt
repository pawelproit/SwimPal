package com.example.swimpal.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository responsible for updating and maintaining user training statistics in Firestore.
 *
 * It increments counters for custom and generated trainings, updates the total number
 * of trainings, and tracks unique active training days for the current user.
 */
class UserStatsRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val user get() = auth.currentUser

    /**
     * Increments the appropriate training counters and updates user statistics.
     *
     * Depending on [isCustom], this method increments either the custom or generated
     * training counter, recalculates the total number of trainings and active days,
     * and writes the updated values back to the user document in Firestore.
     *
     * @param isCustom True if the completed training is custom, false if it is generated.
     * @param onSuccess Callback invoked after successful update of user statistics.
     * @param onError Callback invoked when the user is not logged in or the update fails.
     */
    fun incrementCount(
        isCustom: Boolean,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val u = user ?: run {
            onError(Exception("Nie zalogowano użytkownika"))
            return
        }

        val userDoc = db.collection("users").document(u.uid)

        userDoc.get()
            .addOnSuccessListener { doc ->
                val profile = doc.data

                val currentCustom = (profile?.get("customCount") as? Long ?: 0L)
                val currentGenerated = (profile?.get("generatedCount") as? Long ?: 0L)

                val customCount = currentCustom + if (isCustom) 1 else 0
                val generatedCount = currentGenerated + if (!isCustom) 1 else 0
                val totalCount = customCount + generatedCount

                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val trainingDates = ((profile?.get("trainingDates") as? List<*>)?.mapNotNull { it as? String }
                    ?: emptyList()).toMutableSet()
                trainingDates.add(today)
                val activeDays = trainingDates.size

                userDoc.update(
                    mapOf(
                        "customCount" to customCount,
                        "generatedCount" to generatedCount,
                        "totalCount" to totalCount,
                        "activeDays" to activeDays,
                        "trainingDates" to trainingDates.toList()
                    )
                )
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onError(e) }
            }
            .addOnFailureListener { e -> onError(e) }
    }
}
