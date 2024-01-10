package io.github.mrsrylm.skso.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier


@Module
@InstallIn(ViewModelComponent::class)
object CoroutineModule {
    @IoDispatcher
    @Provides
    @ViewModelScoped
    fun providesIoDispatcher() = Dispatchers.IO
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class IoDispatcher