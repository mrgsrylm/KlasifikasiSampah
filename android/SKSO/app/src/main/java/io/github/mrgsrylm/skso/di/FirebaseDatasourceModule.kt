package io.github.mrgsrylm.skso.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.github.mrgsrylm.skso.data.datasource.remote.FirebaseDatasource
import io.github.mrgsrylm.skso.data.datasource.remote.FirebaseDatasourceImpl

@Module
@InstallIn(ViewModelComponent::class)
abstract class FirebaseDatasourceModule {
    @Binds
    @ViewModelScoped
    abstract fun bindDatasource(datasource: FirebaseDatasourceImpl): FirebaseDatasource
}