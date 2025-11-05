package com.example.swimpal.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class UserStatsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val user get() = auth.currentUser

    fun incrementCustomCount(onComplete: () -> Unit) {
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

    fun incrementGeneratedCount(onComplete: () -> Unit) {
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
}
