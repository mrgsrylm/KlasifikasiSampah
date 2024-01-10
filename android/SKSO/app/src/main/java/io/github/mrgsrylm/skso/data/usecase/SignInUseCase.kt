package io.github.mrgsrylm.skso.data.usecase

import io.github.mrgsrylm.skso.data.model.SignInParam
import io.github.mrgsrylm.skso.data.model.UserModel
import io.github.mrgsrylm.skso.data.repository.FirebaseRepository
import javax.inject.Inject

interface SignInUseCase {
    suspend operator fun invoke(
        param: SignInParam,
        onSuccess: (UserModel) -> Unit,
        onFailure: (String) -> Unit
    )
}

class SignInUseCaseImpl @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : SignInUseCase {
    override suspend fun invoke(
        param: SignInParam,
        onSuccess: (UserModel) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firebaseRepository.signIn(param, onSuccess, onFailure)
    }
}