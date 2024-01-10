package io.github.mrsrylm.skso.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.github.mrsrylm.skso.data.repository_impl.ReqresInRepositoryImpl
import io.github.mrsrylm.skso.domain.repository.ReqresInRepository

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    @ViewModelScoped
    abstract fun bindReqresInRepository(repo: ReqresInRepositoryImpl): ReqresInRepository
}