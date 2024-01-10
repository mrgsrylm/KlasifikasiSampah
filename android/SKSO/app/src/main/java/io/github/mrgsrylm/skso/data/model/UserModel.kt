package io.github.mrgsrylm.skso.data.model

data class UserModel(
    val id: String,
    val email: String,
    val password: String,
    val token: String?,
    val expiresToken: Long?
)