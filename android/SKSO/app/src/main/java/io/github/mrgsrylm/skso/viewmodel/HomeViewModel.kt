package io.github.mrgsrylm.skso.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mrgsrylm.skso.common.NetworkResponseStatus
import io.github.mrgsrylm.skso.common.ScreenStatus
import io.github.mrgsrylm.skso.data.usecase.CountLogByResultToday
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val countLogByResultToday: CountLogByResultToday
) : ViewModel() {
    private val _organikTotal = MutableLiveData<ScreenStatus<Int>>()
    val organikTotal: MutableLiveData<ScreenStatus<Int>> get() = _organikTotal

    private val _anorganikTotal = MutableLiveData<ScreenStatus<Int>>()
    val anorganikTotal: MutableLiveData<ScreenStatus<Int>> get() = _anorganikTotal

    init {
        countOrganik()
        countAnorganik()
    }

    fun countOrganik() {
        countLogByResultToday("organik").onEach {
            when (it) {
                is NetworkResponseStatus.Load -> _organikTotal.postValue(ScreenStatus.Loading)
                is NetworkResponseStatus.Success -> _organikTotal.postValue(ScreenStatus.Success(it.result))
                is NetworkResponseStatus.Error -> _organikTotal.postValue(
                    ScreenStatus.Error(
                        it.exception.message ?: "An error occurred"
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    fun countAnorganik() {
        countLogByResultToday("anorganik").onEach {
            when (it) {
                is NetworkResponseStatus.Load -> _anorganikTotal.postValue(ScreenStatus.Loading)
                is NetworkResponseStatus.Success -> _anorganikTotal.postValue(
                    ScreenStatus.Success(
                        it.result
                    )
                )

                is NetworkResponseStatus.Error -> _anorganikTotal.postValue(
                    ScreenStatus.Error(
                        it.exception.message ?: "An error occurred"
                    )
                )
            }
        }.launchIn(viewModelScope)
    }
}