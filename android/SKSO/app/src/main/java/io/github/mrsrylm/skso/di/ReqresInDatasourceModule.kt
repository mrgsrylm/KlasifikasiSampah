package io.github.mrsrylm.skso.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.github.mrsrylm.skso.data.datasource.remote.ReqresInDatasource
import io.github.mrsrylm.skso.data.datasource.remote.ReqresInDatasourceImpl

@Module
@InstallIn(ViewModelComponent::class)
abstract class ReqresInDatasourceModule {
    @Binds
    @ViewModelScoped
    abstract fun bindReqresInDatasource(source: ReqresInDatasourceImpl): ReqresInDatasource
}