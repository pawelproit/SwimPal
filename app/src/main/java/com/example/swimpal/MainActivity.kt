package com.example.swimpal

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tworzymy TextView programowo
        val textView = TextView(this)
        textView.text = "Wczytywanie..."
        setContentView(textView)

        try {
            // Inicjalizacja Firebase (ważne!)
            FirebaseApp.initializeApp(this)

            // Pobieranie z Firestore
            val db = FirebaseFirestore.getInstance()

            db.collection("test_collection").document("test_doc")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val tekst = document.getString("test_field") ?: "Brak danych"
                        textView.text = "Z Firestore: $tekst"
                    } else {
                        textView.text = "Dokument nie istnieje"
                    }
                }
                .addOnFailureListener { exception ->
                    textView.text = "Błąd: ${exception.message}"
                }

        } catch (e: Exception) {
            textView.text = "Firebase błąd: ${e.message}"
        }
    }
}
