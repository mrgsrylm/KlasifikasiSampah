package io.github.mrgsrylm.skso.data.usecase

import io.github.mrgsrylm.skso.data.model.UserModel
import io.github.mrgsrylm.skso.data.repository.FirebaseRepository
import javax.inject.Inject

interface SignInAnonUseCase {
    suspend operator fun invoke(
        onSuccess: (UserModel) -> Unit,
        onFailure: (String) -> Unit
    )
}

class SignInAnonUseCaseImpl @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : SignInAnonUseCase {

    override suspend fun invoke(onSuccess: (UserModel) -> Unit, onFailure: (String) -> Unit) {
        firebaseRepository.signInAnon(onSuccess, onFailure)
    }

}