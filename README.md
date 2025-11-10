# MediMate

![MediMate Logo](app/src/main/res/drawable/medimate_logo.png)

MediMate is an ndroid application, built with Kotlin, designed to streamline medical appointment management. The app supports three distinct user roles: **Patient (User)**, **Doctor**, and **Administrator**, each with a dedicated set of features.

This application is built on a modern tech stack, featuring **Jetpack Compose** for the UI and **Firebase** for all backend services (including Authentication, Firestore, Storage, and Cloud Messaging).

---

## 🌟 Key Features

The application is logically divided into three modules based on user roles:

### 🧑‍⚕️ For Patients (Users)
* **Authentication:** Secure account creation and login.
* **Browse Doctors:** Access a list of doctors, view their specializations, and profiles.
* **Book Appointments:** Easily select a doctor, date, and time for an appointment.
* **Manage Appointments:** View upcoming and past appointments.
* **Medical Documentation:** Upload and manage personal medical documents (e.g., test results).
* **Leave Reviews:** Rate and review doctors after a visit.
* **Chat:** Communicate directly with doctors via a built-in chat.
* **Profile Management:** Update personal information and profile picture.

### 🩺 For Doctors
* **Manage Availability:** Set and update weekly schedules and available time slots.
* **View Appointments:** See all upcoming and past appointments with patient details.
* **Chat with Patients:** Respond to patient inquiries securely.
* **View Reviews:** Read and manage patient feedback.
* **Profile Management:** Update professional details and personal data.

### 🖥️ For Administrators
* **User Management:** View all users, edit their data, and manage their documents.
* **Doctor Management:** Add new doctors, edit their profiles, and manage their availability.
* **Review Management:** Moderate and manage all doctor reviews.
* **Add Admins:** Grant administrative privileges to other users.

---

## 🛠️ Tech Stack & Architecture

* **Language** Kotlin
* **UI:** Jetpack Compose
* **Architecture:** MVVM (Model-View-ViewModel)
* **Navigation:** Jetpack Navigation Compose
* **Backend (BaaS):** Firebase
    * **Authentication:** Login and Register.
    * **Firestore:** The main NoSQL database for users, doctors, appointments, and reviews..
    * **Storage:** File storage (medical records, profile photos).
    * **Cloud Messaging (FCM):** Push  Notifications (e.g, chat, appointments).
    * **Cloud Functions:** Backend logic (e.g., sending emails).
* **Asynchronous:** Kotlin Coroutines & Flow.

---

## 🚀 How to Run

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/natalia0sperczynska/MediMate.git](https://github.com/natalia0sperczynska/MediMate.git)
    ```
2.  **Open in Android Studio:**
    Open the cloned directory in Android Studio.
3.  **Sync & Run:**
    * Wait for Android Studio to sync the Gradle files.
    * Build and run the 'app' configuration on an emulator or a physical device.
