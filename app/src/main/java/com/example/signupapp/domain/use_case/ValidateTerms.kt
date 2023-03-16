package com.example.signupapp.domain.use_case

class ValidateTerms {


    fun execute(acceptedTerms: Boolean): ValidationResult {
        if (!acceptedTerms)
            return ValidationResult(
                successful = false,
                errorMessage = "Please accept the terms and conditions"
            )
        else return ValidationResult(successful = true)
    }


}