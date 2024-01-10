package io.github.mrsrylm.skso.domain.repository

import io.github.mrsrylm.skso.common.NetworkResponseStatus
import io.github.mrsrylm.skso.domain.entity.User
import kotlinx.coroutines.flow.Flow


interface ReqresInRepository {
    fun getProductById(id: Int): Flow<NetworkResponseStatus<User>>
}