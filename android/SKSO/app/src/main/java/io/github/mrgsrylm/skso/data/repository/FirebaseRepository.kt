package io.github.mrgsrylm.skso.data.repository

import io.github.mrgsrylm.skso.common.NetworkResponseStatus
import io.github.mrgsrylm.skso.common.TokenManager
import io.github.mrgsrylm.skso.data.datasource.remote.FirebaseDatasource
import io.github.mrgsrylm.skso.data.model.LogModel
import io.github.mrgsrylm.skso.data.model.SignInParam
import io.github.mrgsrylm.skso.data.model.UserModel
import io.github.mrgsrylm.skso.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface FirebaseRepository {
    fun signIn(
        param: SignInParam,
        onSuccess: (UserModel) -> Unit,
        onFailure: (String) -> Unit
    )

    fun signInAnon(onSuccess: (UserModel) -> Unit, onFailure: (String) -> Unit)

    fun countLogByResultToday(result: String): Flow<NetworkResponseStatus<Int>>

    fun findLogs(): Flow<NetworkResponseStatus<List<LogModel>>>
}

class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseDatasource: FirebaseDatasource,
    private val tokenManager: TokenManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : FirebaseRepository {

    override fun signIn(
        param: SignInParam,
        onSuccess: (UserModel) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firebaseDatasource.signIn(
            param,
            onSuccess = { rec ->
                tokenManager.saveToken(
                    token = rec.token ?: "",
                    expires = rec.expiresToken ?: 0
                )
                onSuccess(rec)
            },
            onFailure
        )
    }

    override fun signInAnon(onSuccess: (UserModel) -> Unit, onFailure: (String) -> Unit) {
        firebaseDatasource.signInAnon(
            onSuccess = { rec ->
                tokenManager.saveToken(
                    token = rec.token ?: "",
                    expires = rec.expiresToken ?: 0
                )
                onSuccess(rec)
            },
            onFailure
        )
    }

    override fun countLogByResultToday(result: String): Flow<NetworkResponseStatus<Int>> {
        return firebaseDatasource.countLogOfResult(result).map {
            when (it) {
                is NetworkResponseStatus.Load -> NetworkResponseStatus.Load
                is NetworkResponseStatus.Success -> NetworkResponseStatus.Success(it.result)
                is NetworkResponseStatus.Error -> NetworkResponseStatus.Error(it.exception)
            }
        }.flowOn(ioDispatcher)
    }

    override fun findLogs(): Flow<NetworkResponseStatus<List<LogModel>>> {
        return firebaseDatasource.findLogs().map {
            when (it) {
                is NetworkResponseStatus.Load -> NetworkResponseStatus.Load
                is NetworkResponseStatus.Success -> NetworkResponseStatus.Success(it.result)
                is NetworkResponseStatus.Error -> NetworkResponseStatus.Error(it.exception)
            }
        }.flowOn(ioDispatcher)
    }

}