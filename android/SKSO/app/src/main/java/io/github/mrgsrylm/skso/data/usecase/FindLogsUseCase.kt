package io.github.mrgsrylm.skso.data.usecase

import io.github.mrgsrylm.skso.common.NetworkResponseStatus
import io.github.mrgsrylm.skso.data.model.LogModel
import io.github.mrgsrylm.skso.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface FindLogsUseCase {
    operator fun invoke(): Flow<NetworkResponseStatus<List<LogModel>>>
}

class FindLogsUseCaseImpl @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : FindLogsUseCase {

    override fun invoke(): Flow<NetworkResponseStatus<List<LogModel>>> {
        return firebaseRepository.findLogs()
    }

}