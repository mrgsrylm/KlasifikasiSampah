package io.github.mrsrylm.skso.data.payload

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.mrsrylm.skso.domain.entity.User

@JsonClass(generateAdapter = true)
data class UserPayload(
    @Json(name = "id")
    val id: Int,

    @Json(name = "name")
    val name: String,

    @Json(name = "username")
    val username: String,

    @Json(name = "email")
    val email: String,
)

fun UserPayload.toUser(): User {
    return User(
        id = this.id,
        name = this.name,
        username = this.username,
        email = this.email,
    )
}