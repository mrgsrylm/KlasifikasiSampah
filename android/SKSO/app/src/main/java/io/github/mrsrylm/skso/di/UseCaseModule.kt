package io.github.mrsrylm.skso.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.github.mrsrylm.skso.domain.usecase.GetUserUseCase
import io.github.mrsrylm.skso.domain.usecase.GetUserUseCaseImpl

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {
    @Binds
    @ViewModelScoped
    abstract fun bindGetUserUseCase(usecase: GetUserUseCaseImpl): GetUserUseCase
}