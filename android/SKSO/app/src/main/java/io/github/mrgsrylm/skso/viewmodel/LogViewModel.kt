package io.github.mrgsrylm.skso.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mrgsrylm.skso.common.NetworkResponseStatus
import io.github.mrgsrylm.skso.common.ScreenStatus
import io.github.mrgsrylm.skso.data.model.LogModel
import io.github.mrgsrylm.skso.data.usecase.FindLogsUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    private val findLogsUseCase: FindLogsUseCase
) : ViewModel() {
    private val _logModels = MutableLiveData<ScreenStatus<List<LogModel>>>()
    val logModels: LiveData<ScreenStatus<List<LogModel>>> get() = _logModels

    init {
        findLogs()
    }

    private fun findLogs() {
        findLogsUseCase().onEach {
            when (it) {
                is NetworkResponseStatus.Load -> _logModels.postValue(ScreenStatus.Loading)
                is NetworkResponseStatus.Success -> _logModels.postValue(ScreenStatus.Success(it.result))
                is NetworkResponseStatus.Error -> _logModels.postValue(
                    ScreenStatus.Error(
                        it.exception.message ?: "An error occurred"
                    )
                )
            }
        }.launchIn(viewModelScope)
    }


}