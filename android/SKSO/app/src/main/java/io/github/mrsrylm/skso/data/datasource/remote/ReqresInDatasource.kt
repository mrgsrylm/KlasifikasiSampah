package io.github.mrsrylm.skso.data.datasource.remote

import io.github.mrsrylm.skso.common.NetworkResponseStatus
import io.github.mrsrylm.skso.data.payload.UserPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.GET
import retrofit2.http.Path
import javax.inject.Inject

interface ReqresInAPI {
    @GET("/users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserPayload

}

interface ReqresInDatasource {
    fun getUserById(id: Int): Flow<NetworkResponseStatus<UserPayload>>
}

class ReqresInDatasourceImpl @Inject constructor(
    private val api: ReqresInAPI
) : ReqresInDatasource {
    override fun getUserById(id: Int): Flow<NetworkResponseStatus<UserPayload>> {
        return flow {
            emit(NetworkResponseStatus.Load)
            try {
                val response = api.getUserById(id)
                emit(NetworkResponseStatus.Success(response))
            } catch (e: Exception) {
                emit(NetworkResponseStatus.Error(e))
            }
        }
    }

}
