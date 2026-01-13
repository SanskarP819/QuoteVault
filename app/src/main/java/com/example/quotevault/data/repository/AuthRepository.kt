package com.example.quotevault.data.repository

import com.example.quotevault.data.remote.SupabaseClientWrapper
import com.example.quotevault.domain.model.User
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

@Singleton
class AuthRepository @Inject constructor(
    private val supabase: SupabaseClientWrapper
) {

    // FIXED: Sign up with auto-login
    suspend fun signUp(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            // Step 1: Sign up the user
            supabase.client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            // Step 2: Wait for signup to complete
            delay(1500) // Increased delay for Supabase to process

            // Step 3: Automatically sign in the user
            val signInResult = supabase.client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            // Step 4: Get the current user
            delay(500) // Wait for session to establish

            val currentUser = supabase.client.auth.currentUserOrNull()

            if (currentUser != null) {
                AuthResult.Success(
                    User(
                        id = currentUser.id,
                        email = currentUser.email ?: email,
                        name = currentUser.userMetadata?.get("name") as? String
                    )
                )
            } else {
                AuthResult.Error("Account created but automatic login failed. Please login manually.")
            }
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("already registered") == true ->
                    "This email is already registered. Please login instead."
                e.message?.contains("Invalid email") == true ->
                    "Please enter a valid email address."
                e.message?.contains("Password") == true ->
                    "Password must be at least 6 characters."
                else -> e.message ?: "Sign up failed. Please try again."
            }
            AuthResult.Error(errorMessage)
        }
    }

    suspend fun signIn(email: String, password: String): AuthResult =
        withContext(Dispatchers.IO) {
            try {
                supabase.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                delay(500)

                val user = supabase.client.auth.currentUserOrNull()
                    ?: return@withContext AuthResult.Error(
                        "Login succeeded but user not found"
                    )

                AuthResult.Success(
                    User(id = user.id, email = user.email ?: email)
                )
            } catch (e: Exception) {
                AuthResult.Error(e.message ?: "Login failed")
            }
        }

    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabase.client.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                supabase.client.auth.resetPasswordForEmail(email)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    fun getCurrentUser(): User? {
        val user = supabase.client.auth.currentUserOrNull() ?: return null
        return User(
            id = user.id,
            email = user.email ?: "",
            name = user.userMetadata?.get("name") as? String
        )
    }

    fun isUserLoggedIn(): Boolean =
        supabase.client.auth.currentUserOrNull() != null

    // FIXED: Using retrieveUser after OTP verification
    suspend fun verifyRecoveryToken(token: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                // Try to retrieve the user session using the recovery token
                supabase.client.auth.retrieveUser(token)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    suspend fun updatePassword(newPassword: String): AuthResult =
        withContext(Dispatchers.IO) {
            try {
                supabase.client.auth.updateUser {
                    password = newPassword
                }
                val user = supabase.client.auth.currentUserOrNull()
                    ?: return@withContext AuthResult.Error("User not found after password update")

                AuthResult.Success(
                    User(id = user.id, email = user.email ?: "")
                )
            } catch (e: Exception) {
                AuthResult.Error(e.message ?: "Password update failed")
            }
        }
}