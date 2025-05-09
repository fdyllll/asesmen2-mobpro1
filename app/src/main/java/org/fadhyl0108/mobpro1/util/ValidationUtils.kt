package org.fadhyl0108.mobpro1.util

object ValidationUtils {
    fun validateNotEmpty(value: String): Boolean {
        return value.trim().isNotEmpty()
    }

    fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) return true // Email opsional
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$".toRegex()
        return emailRegex.matches(email)
    }

    fun validatePhoneNumber(phone: String): Boolean {
        if (phone.isEmpty()) return false
        val phoneRegex = "^[0-9]{10,15}\$".toRegex()
        return phoneRegex.matches(phone)
    }

    fun validateMinLength(text: String, minLength: Int): Boolean {
        return text.trim().length >= minLength
    }

    fun validateMaxLength(text: String, maxLength: Int): Boolean {
        return text.trim().length <= maxLength
    }

    fun validateNumeric(text: String): Boolean {
        return text.matches(Regex("^[0-9]+$"))
    }

    fun validateAlphanumeric(text: String): Boolean {
        return text.matches(Regex("^[a-zA-Z0-9]+$"))
    }
} 