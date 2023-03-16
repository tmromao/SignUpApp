package com.example.signupapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signupapp.domain.use_case.ValidateEmail
import com.example.signupapp.domain.use_case.ValidatePassword
import com.example.signupapp.domain.use_case.ValidateRepeatedPassword
import com.example.signupapp.domain.use_case.ValidateTerms
import com.example.signupapp.presentation.RegistrationFormEvent
import com.example.signupapp.presentation.RegistrationFormState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val validateEmail: ValidateEmail = ValidateEmail(),
    private val validatePassword: ValidatePassword = ValidatePassword(),
    private val validateRepeatedPassword: ValidateRepeatedPassword = ValidateRepeatedPassword(),
    private val validateAcceptedTerms: ValidateTerms = ValidateTerms(),
) : ViewModel() {

    var state by mutableStateOf(RegistrationFormState())

    private val validateEventChannel = Channel<ValidationEvent>()
    val validateEvent = validateEventChannel.receiveAsFlow()

    fun onEvent(event: RegistrationFormEvent) {
        when (event) {
            is RegistrationFormEvent.AcceptedTermsChanged -> state =
                state.copy(acceptedTerms = event.acceptedTerms)
            is RegistrationFormEvent.EmailChanged -> state = state.copy(email = event.email)
            is RegistrationFormEvent.PasswordChanged -> state =
                state.copy(password = event.password)
            is RegistrationFormEvent.RepeatedPasswordChanged -> state =
                state.copy(repeatedPassword = event.repeatedPassword)
            is RegistrationFormEvent.RegistrationButtonClicked -> submitData()
        }
    }

    private fun submitData() {
        val emailResult = validateEmail.execute(state.email)
        val passwordResult = validatePassword.execute(state.password)
        val repeatedPasswordResult =
            validateRepeatedPassword.execute(state.password, state.repeatedPassword)
        val acceptedTermsResult = validateAcceptedTerms.execute(state.acceptedTerms)

        val hasError =
            listOf(emailResult, passwordResult, repeatedPasswordResult, acceptedTermsResult)
                .any { !it.successful }
        if (hasError) {
            state = state.copy(
                emailError = emailResult.errorMessage,
                passwordError = passwordResult.errorMessage,
                repeatedPasswordError = repeatedPasswordResult.errorMessage,
                acceptedTermsError = acceptedTermsResult.errorMessage
            )
        }
        viewModelScope.launch {
            validateEventChannel.send(ValidationEvent.Success)
        }
    }

    sealed class ValidationEvent() {
        object Success : ValidationEvent()
        data class Error(val errorMessage: String) : ValidationEvent()
    }

}