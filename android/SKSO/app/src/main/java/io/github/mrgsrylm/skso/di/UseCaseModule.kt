package io.github.mrgsrylm.skso.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.github.mrgsrylm.skso.data.usecase.CountLogByResultToday
import io.github.mrgsrylm.skso.data.usecase.CountLogByResultTodayImpl
import io.github.mrgsrylm.skso.data.usecase.FindLogsUseCase
import io.github.mrgsrylm.skso.data.usecase.FindLogsUseCaseImpl
import io.github.mrgsrylm.skso.data.usecase.SignInAnonUseCase
import io.github.mrgsrylm.skso.data.usecase.SignInAnonUseCaseImpl
import io.github.mrgsrylm.skso.data.usecase.SignInUseCase
import io.github.mrgsrylm.skso.data.usecase.SignInUseCaseImpl

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {
    @Binds
    @ViewModelScoped
    abstract fun bindSignInUseCase(usecase: SignInUseCaseImpl): SignInUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindSignInAnonUseCase(usecase: SignInAnonUseCaseImpl): SignInAnonUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindCountLogByResultToday(usecase: CountLogByResultTodayImpl): CountLogByResultToday

    @Binds
    @ViewModelScoped
    abstract fun bindFindLogsUseCase(usecase: FindLogsUseCaseImpl): FindLogsUseCase
}