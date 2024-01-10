package io.github.mrsrylm.skso.ui.screen.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mrsrylm.skso.common.NetworkResponseStatus
import io.github.mrsrylm.skso.common.ScreenStatus
import io.github.mrsrylm.skso.domain.entity.User
import io.github.mrsrylm.skso.domain.usecase.GetUserUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserById: GetUserUseCase,
) : ViewModel() {
    private val _user = MutableLiveData<ScreenStatus<User>>()
    val user: LiveData<ScreenStatus<User>> get() = _user

    init {
        getUserById(1)
    }

    private fun getUserById(id: Int) {
        viewModelScope.launch {
            getUserById.invoke(id).onEach {
                when (it) {
                    is NetworkResponseStatus.Load -> _user.postValue(ScreenStatus.Loading)
                    is NetworkResponseStatus.Success -> _user.postValue(ScreenStatus.Success(it.result))
                    is NetworkResponseStatus.Error -> _user.postValue(ScreenStatus.Error(it.exception.message!!))
                }
            }.launchIn(viewModelScope)
        }
    }
}



