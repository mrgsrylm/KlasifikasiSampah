package io.github.mrgsrylm.skso.data.datasource.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.github.mrgsrylm.skso.common.NetworkResponseStatus
import io.github.mrgsrylm.skso.data.model.LogModel
import io.github.mrgsrylm.skso.data.model.SignInParam
import io.github.mrgsrylm.skso.data.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface FirebaseDatasource {
    fun signIn(
        param: SignInParam,
        onSuccess: (UserModel) -> Unit,
        onFailure: (String) -> Unit
    )

    fun signInAnon(onSuccess: (UserModel) -> Unit, onFailure: (String) -> Unit)

    fun countLogOfResult(result: String): Flow<NetworkResponseStatus<Int>>

    fun findLogs(): Flow<NetworkResponseStatus<List<LogModel>>>

    fun findLogByDocId(docId: String): Flow<NetworkResponseStatus<LogModel>>
}

class FirebaseDatasourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    fireStore: FirebaseFirestore,
) : FirebaseDatasource {
    private val logsCollection: CollectionReference = fireStore.collection(LOGS_COLLECTION)

    // sign in firebase with email, password
    override fun signIn(
        param: SignInParam,
        onSuccess: (UserModel) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(param.email, param.password)
            .addOnSuccessListener {
                val firebaseUser = it.user
                firebaseUser?.getIdToken(true)
                    ?.addOnSuccessListener { result ->
                        val token = result.token
                        val expiresToken = result.expirationTimestamp
                        onSuccess(
                            UserModel(
                                id = firebaseUser.uid ?: "",
                                email = firebaseUser.email ?: "",
                                password = "",
                                token = token ?: "",
                                expiresToken = expiresToken ?: 0
                            ),
                        )
                    }

            }
            .addOnFailureListener {
                onFailure(it.message ?: "An error occurred")
            }
    }

    // sign in firebase with anonymous
    override fun signInAnon(
        onSuccess: (UserModel) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firebaseAuth.signInAnonymously()
            .addOnSuccessListener {
                val firebaseUser = it.user
                firebaseUser?.getIdToken(true)
                    ?.addOnSuccessListener { result ->
                        val token = result.token
                        val expiresToken = result.expirationTimestamp
                        onSuccess(
                            UserModel(
                                id = firebaseUser.uid ?: "",
                                email = firebaseUser.email ?: "",
                                password = "",
                                token = token ?: "",
                                expiresToken = expiresToken ?: 0
                            ),
                        )
                    }

            }
            .addOnFailureListener {
                onFailure(it.message ?: "An error occurred")
            }
    }

    override fun countLogOfResult(result: String): Flow<NetworkResponseStatus<Int>> {
        return flow {
            try {
                emit(NetworkResponseStatus.Load)

                val query = logsCollection.whereEqualTo(RESULT_FIELD, result).count()
                val total: Int = query.get(AggregateSource.SERVER).await().count.toInt()

                emit(NetworkResponseStatus.Success(total))
            } catch (exception: Exception) {
                emit(NetworkResponseStatus.Error(exception))
            }
        }
    }

    override fun findLogs(): Flow<NetworkResponseStatus<List<LogModel>>> {
        return flow {
            try {
                emit(NetworkResponseStatus.Load)

                val logModels = mutableListOf<LogModel>()
                val querySnapshot: QuerySnapshot = logsCollection.get().await()
                for (doc in querySnapshot) {
                    val logModel = LogModel(
                        docId = doc.id,
                        classifiedAt = doc.getLong(CLASSIFIED_AT_FIELD) ?: 0,
                        image = doc.getString(IMAGE_FIELD) ?: "",
                        result = doc.getString(RESULT_FIELD) ?: ""
                    )
                    logModels.add(logModel)
                }

                emit(NetworkResponseStatus.Success(logModels))
            } catch (exception: Exception) {
                emit(NetworkResponseStatus.Error(exception))
            }
        }
    }

    override fun findLogByDocId(docId: String): Flow<NetworkResponseStatus<LogModel>> {
        return flow {
            try {
                emit(NetworkResponseStatus.Load)

                val doc = logsCollection.document(docId).get().await()
                val logModel = LogModel(
                    docId = doc.id,
                    classifiedAt = doc.getLong(CLASSIFIED_AT_FIELD) ?: 0,
                    image = doc.getString(IMAGE_FIELD) ?: "",
                    result = doc.getString(RESULT_FIELD) ?: ""
                )

                emit(NetworkResponseStatus.Success(logModel))
            } catch (exception: Exception) {
                emit(NetworkResponseStatus.Error(exception))
            }
        }
    }

    companion object {
        private const val LOGS_COLLECTION = "logs/(default)/doc_id"
        private const val CLASSIFIED_AT_FIELD = "classified_at"
        private const val IMAGE_FIELD = "image"
        private const val RESULT_FIELD = "result"
    }
}

