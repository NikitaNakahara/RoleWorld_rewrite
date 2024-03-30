package com.nakaharadev.roleworld.network

object AuthRequest {
    data class SignIn(
        val email: String,
        val password: String
    )

    data class SignUp(
        val nickname: String,
        val email: String,
        val password: String
    )
}