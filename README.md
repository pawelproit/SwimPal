# SwimPal

**SwimPal** is an Android mobile application built with Kotlin, Jetpack Compose, and Firebase, designed for generating personalized swimming training plans based on training days, swimming level, and goals. The app supports both recreational and competitive swimmers.

## 🎯 Key Features

- **Training Plan Generation** – Creates structured workouts based on days per week, skill level (1-3), and training goal
- **Custom Workout Creation** – Manual exercise set definition
- **Achievements System** – Motivation through badge collection
- **Training History** – Progress tracking with dates
- **Instructional Video Library** – Swimming technique tutorials
- **User Profile** – Personalization with stats and demographics

## 📱 Tech Stack

- **Frontend**: Kotlin, Jetpack Compose, MVVM architecture
- **Backend**: Firebase (Authentication, Firestore, Test Lab)
- **Testing**: Test Lab

## 🗄️ Firebase Data Structure

### Training Collections

**12 Training Categories** (each with 3 difficulty levels):

| Category Prefix | Description |
|-----------------|-------------|
| `open_water_level_1/2/3` | Open water training |
| `sprints_level_1/2/3` | Sprint training |
| `technique_level_1/2/3` | Technique-focused training |
| `triathlon_level_1/2/3` | Triathlon training |

**Training Document Structure**:
- Each training document contains exactly **6 exercise objects**
- Each exercise object includes: `nazwa` (name), `opis` (description), `order` (sequence number 1-6)

### `users` Collection

**UserProfile Data Model**:

```kotlin
data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val customCount: Int = 0,
    val generatedCount: Int = 0,
    val totalCount: Int = 0,
    val activeDays: Int = 0,
    val trainingDates: List<String> = emptyList(),
    val badges: List<Badge> = emptyList()
)
```

**User Sub-collections**:
- `custom_trainings` – User-created workouts
- `generated_trainings` – Algorithm-generated workout plans
- `history_trainings` – Completed training sessions

## 🏆 Achievements System

- **Custom**: 5, 10, 20, 50 custom trainings
- **Generated**: 5, 10, 20, 50 generated trainings
- **Total**: 20, 50, 100 total trainings
- **Active Days**: 5, 20, 50 active training days

## 🚀 Quick Start

```bash
git clone <repository-url>
cd SwimPal
./gradlew build
```

**Requirements**: Android Studio, configured Firebase project

## 📄 Academic Documentation

Engineering thesis project at University of Silesia (2026).