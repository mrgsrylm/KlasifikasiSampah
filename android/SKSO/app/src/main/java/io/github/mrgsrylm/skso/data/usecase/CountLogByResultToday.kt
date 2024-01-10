package io.github.mrgsrylm.skso.data.usecase

import io.github.mrgsrylm.skso.common.NetworkResponseStatus
import io.github.mrgsrylm.skso.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface CountLogByResultToday {
    operator fun invoke(result: String): Flow<NetworkResponseStatus<Int>>
}

class CountLogByResultTodayImpl @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : CountLogByResultToday {

    override fun invoke(result: String): Flow<NetworkResponseStatus<Int>> {
        return firebaseRepository.countLogByResultToday(result)
    }

}