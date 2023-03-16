package com.example.signupapp.presentation

sealed class RegistrationFormEvent {
    data class EmailChanged(val email: String) : RegistrationFormEvent()
    data class PasswordChanged(val password: String) : RegistrationFormEvent()
    data class RepeatedPasswordChanged(val repeatedPassword: String) : RegistrationFormEvent()

    data class AcceptedTermsChanged(val acceptedTerms: Boolean) : RegistrationFormEvent()

    object RegistrationButtonClicked : RegistrationFormEvent()

}