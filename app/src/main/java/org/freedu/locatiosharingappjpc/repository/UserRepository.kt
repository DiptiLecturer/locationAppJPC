package org.freedu.locatiosharingappjpc.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.freedu.locatiosharingappjpc.model.AppUsers

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // ─── Email / Password Sign-Up ───────────────────────────────────────────
    suspend fun signUp(email: String, password: String): Result<AppUsers> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser =
                result.user ?: return Result.failure(Exception("User creation failed"))

            val username = email.substringBefore("@")

            val appUser = AppUsers(
                userId = firebaseUser.uid,
                email = email,
                username = username
            )

            saveUserToFirestore(appUser)
            Result.success(appUser)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Email / Password Login ─────────────────────────────────────────────
    suspend fun login(email: String, password: String): Result<AppUsers> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("Login failed"))

            // Fetch existing Firestore doc
            val doc = firestore.collection("AppUsers")
                .document(firebaseUser.uid)
                .get()
                .await()

            val appUser = doc.toObject(AppUsers::class.java)
                ?: AppUsers(
                    userId = firebaseUser.uid,
                    email = email,
                    username = email.substringBefore("@")
                )

            Result.success(appUser)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Google Sign-In ─────────────────────────────────────────────────────
    suspend fun signInWithGoogle(idToken: String): Result<AppUsers> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser =
                result.user ?: return Result.failure(Exception("Google sign-in failed"))

            val email = firebaseUser.email ?: ""
            val username = email.substringBefore("@")

            // Only create Firestore doc if this is a new user
            val docRef = firestore.collection("AppUsers").document(firebaseUser.uid)
            val existing = docRef.get().await()

            val appUser = if (!existing.exists()) {
                val newUser = AppUsers(
                    userId = firebaseUser.uid,
                    email = email,
                    username = username
                )
                saveUserToFirestore(newUser)
                newUser
            } else {
                existing.toObject(AppUsers::class.java)
                    ?: AppUsers(firebaseUser.uid, email, username)
            }

            Result.success(appUser)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Logout ─────────────────────────────────────────────────────────────
    fun logout() = auth.signOut()

    // ─── Already logged in? ──────────────────────────────────────────────────
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private suspend fun saveUserToFirestore(user: AppUsers) {
        firestore.collection("AppUsers")
            .document(user.userId)
            .set(user)
            .await()
    }

    suspend fun getCurrentUserData(uid: String): Result<AppUsers> {
        return try {
            val doc = firestore.collection("AppUsers")
                .document(uid)
                .get()
                .await()

            val user = doc.toObject(AppUsers::class.java)
                ?: return Result.failure(Exception("User not found"))

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllOtherUsers(uid: String): Result<List<AppUsers>> {
        return try {
            val snapshot = firestore.collection("AppUsers")
                .get()
                .await()

            val users = snapshot.documents
                .mapNotNull { it.toObject(AppUsers::class.java) }
                .filter { it.userId != uid } // exclude current user

            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Add these to your UserRepository class
    suspend fun updateUserName(uid: String, newName: String): Result<Unit> {
        return try {
            firestore.collection("AppUsers")
                .document(uid)
                .update("username", newName)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserLocation(uid: String, lat: Double, lng: Double): Result<Unit> {
        return try {
            firestore.collection("AppUsers")
                .document(uid)
                .update(
                    mapOf(
                        "latitude" to lat,
                        "longitude" to lng
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}