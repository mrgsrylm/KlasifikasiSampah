package io.github.mrgsrylm.skso.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.github.mrgsrylm.skso.data.repository.FirebaseRepository
import io.github.mrgsrylm.skso.data.repository.FirebaseRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    @ViewModelScoped
    abstract fun bindFirebaseRepository(repository: FirebaseRepositoryImpl): FirebaseRepository
}