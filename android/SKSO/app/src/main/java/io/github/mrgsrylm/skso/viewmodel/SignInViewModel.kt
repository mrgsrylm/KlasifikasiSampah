package io.github.mrgsrylm.skso.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mrgsrylm.skso.common.Constants
import io.github.mrgsrylm.skso.common.ScreenStatus
import io.github.mrgsrylm.skso.data.model.SignInParam
import io.github.mrgsrylm.skso.data.model.UserModel
import io.github.mrgsrylm.skso.data.usecase.SignInAnonUseCase
import io.github.mrgsrylm.skso.data.usecase.SignInUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signInAnonUseCase: SignInAnonUseCase,
    private val sharedPrefs: SharedPreferences,
) : ViewModel() {
    private val _signInState = MutableLiveData<ScreenStatus<UserModel>>()
    val signInState: LiveData<ScreenStatus<UserModel>> get() = _signInState

    init {
        signIn(param = SignInParam("admin@skso.com", "passwd123"))
    }

    fun signIn(param: SignInParam) {
        viewModelScope.launch {
            _signInState.postValue(ScreenStatus.Loading)

            signInUseCase.invoke(
                param,
                onSuccess = {
                    _signInState.postValue(
                        ScreenStatus.Success(it)
                    )
                    saveUserSharedPref(it.id)
                }
            ) {
                signInAnon()
            }
        }
    }

    fun signInAnon() {
        viewModelScope.launch {
            _signInState.postValue(ScreenStatus.Loading)

            signInAnonUseCase.invoke(
                onSuccess = {
                    _signInState.postValue(
                        ScreenStatus.Success(it)
                    )
                    saveUserSharedPref(it.id)
                }
            ) {
                _signInState.postValue(ScreenStatus.Error(it))
            }
        }
    }

    // private
    private fun saveUserSharedPref(id: String) {
        sharedPrefs.edit()
            .putString(Constants.PREF_USERID_KEY, id)
            .apply()
    }
}