package uk.ac.tees.mad.dealzone.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    private fun mapFirebaseException(e: Exception): Exception {
        val message = when {
            e.message?.contains("email address is already in use") == true ->
                "This email is already registered. Please sign in."
            e.message?.contains("password is invalid") == true ||
                    e.message?.contains("INVALID_PASSWORD") == true ->
                "Incorrect password. Please try again."
            e.message?.contains("no user record") == true ||
                    e.message?.contains("EMAIL_NOT_FOUND") == true ->
                "No account found with this email. Please register."
            e.message?.contains("badly formatted") == true ->
                "Invalid email format."
            e.message?.contains("network") == true ||
                    e.message?.contains("NETWORK") == true ->
                "No internet connection. Please check your network."
            else -> e.message ?: "Authentication failed. Please try again."
        }
        return Exception(message)
    }
}