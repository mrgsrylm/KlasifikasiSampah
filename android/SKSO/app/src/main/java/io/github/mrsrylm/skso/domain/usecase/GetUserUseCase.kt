package io.github.mrsrylm.skso.domain.usecase

import io.github.mrsrylm.skso.common.NetworkResponseStatus
import io.github.mrsrylm.skso.domain.entity.User
import io.github.mrsrylm.skso.domain.repository.ReqresInRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetUserUseCase {
    operator fun invoke(id: Int): Flow<NetworkResponseStatus<User>>
}

class GetUserUseCaseImpl @Inject constructor(
    private val repo: ReqresInRepository
) : GetUserUseCase {
    override fun invoke(id: Int): Flow<NetworkResponseStatus<User>> {
        return repo.getProductById(id)
    }
}