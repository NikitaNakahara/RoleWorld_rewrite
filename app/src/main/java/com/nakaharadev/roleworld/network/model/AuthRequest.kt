package com.nakaharadev.roleworld.network.model

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