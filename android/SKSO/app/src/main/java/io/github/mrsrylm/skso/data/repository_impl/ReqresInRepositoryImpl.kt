package io.github.mrsrylm.skso.data.repository_impl

import io.github.mrsrylm.skso.common.NetworkResponseStatus
import io.github.mrsrylm.skso.data.datasource.remote.ReqresInDatasource
import io.github.mrsrylm.skso.data.payload.toUser
import io.github.mrsrylm.skso.di.IoDispatcher
import io.github.mrsrylm.skso.domain.entity.User
import io.github.mrsrylm.skso.domain.repository.ReqresInRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReqresInRepositoryImpl @Inject constructor(
    private val reqresInDatasource: ReqresInDatasource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ReqresInRepository {
    override fun getProductById(id: Int): Flow<NetworkResponseStatus<User>> {
        return reqresInDatasource.getUserById(id).map {
            when (it) {
                is NetworkResponseStatus.Load -> NetworkResponseStatus.Load
                is NetworkResponseStatus.Success -> NetworkResponseStatus.Success(it.result.toUser())
                is NetworkResponseStatus.Error -> NetworkResponseStatus.Error(it.exception)
            }
        }.flowOn(ioDispatcher)
    }
}