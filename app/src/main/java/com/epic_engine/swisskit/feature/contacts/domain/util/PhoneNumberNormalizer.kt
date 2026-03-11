package com.epic_engine.swisskit.feature.contacts.domain.util

object PhoneNumberNormalizer {

    private const val MAX_DIGITS = 15

    fun normalize(raw: String): String {
        val trimmed = raw.trim()
        val cleaned = trimmed.replace(Regex("[^0-9+]"), "")
        val withPrefix = if (cleaned.startsWith("+")) cleaned else "+$cleaned"
        val digits = withPrefix.removePrefix("+").take(MAX_DIGITS)
        return "+$digits"
    }

    fun isValid(phone: String): Boolean {
        val digits = phone.filter { it.isDigit() }
        return digits.length in 7..MAX_DIGITS
    }

    fun toCallUrl(phone: String): String = "tel:${normalize(phone)}"

    fun toWhatsAppUrl(phone: String): String {
        val digits = normalize(phone).removePrefix("+")
        return "https://wa.me/$digits"
    }
}
