package com.epic_engine.swisskit.feature.qrscanner.domain.detector

import android.util.Patterns
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRContentType

object QRContentDetector {

    fun detect(content: String): QRContentType = when {
        isWifi(content)     -> QRContentType.WIFI
        isContact(content)  -> QRContentType.CONTACT
        isCalendar(content) -> QRContentType.CALENDAR
        isEmail(content)    -> QRContentType.EMAIL
        isPhone(content)    -> QRContentType.PHONE
        isLocation(content) -> QRContentType.LOCATION
        isUrl(content)      -> QRContentType.URL
        isBarcode(content)  -> QRContentType.BARCODE
        else                -> QRContentType.TEXT
    }

    fun generateLabel(content: String, type: QRContentType): String = when (type) {
        QRContentType.URL      -> extractDomain(content)?.let { "Enlace: $it" } ?: "Enlace web"
        QRContentType.WIFI     -> extractWifiSsid(content)?.let { "WiFi: $it" } ?: "Red WiFi"
        QRContentType.CONTACT  -> "Contacto"
        QRContentType.EMAIL    -> extractEmail(content)?.let { "Email: $it" } ?: "Correo electrónico"
        QRContentType.PHONE    -> extractPhone(content)?.let { "Tel: $it" } ?: "Número de teléfono"
        QRContentType.LOCATION -> "Ubicación"
        QRContentType.CALENDAR -> "Evento de calendario"
        QRContentType.BARCODE  -> "Código: $content"
        QRContentType.TEXT     -> if (content.length > 40) "${content.take(37)}..." else content
    }

    private fun isUrl(c: String) =
        Patterns.WEB_URL.matcher(c.trim()).matches()

    private fun isWifi(c: String) =
        c.trimStart().startsWith("WIFI:", ignoreCase = true)

    private fun isContact(c: String) =
        c.contains("BEGIN:VCARD", ignoreCase = true) ||
        c.trimStart().startsWith("MECARD:", ignoreCase = true)

    private fun isEmail(c: String) =
        c.trimStart().startsWith("mailto:", ignoreCase = true) ||
        c.trimStart().startsWith("MATMSG:", ignoreCase = true) ||
        Patterns.EMAIL_ADDRESS.matcher(c.trim()).matches()

    private fun isPhone(c: String) =
        c.trimStart().startsWith("tel:", ignoreCase = true) ||
        Regex("^\\+?[0-9\\s\\-().]{7,20}$").matches(c.trim())

    private fun isLocation(c: String) =
        c.trimStart().startsWith("geo:", ignoreCase = true) ||
        c.contains("maps.google.com", ignoreCase = true) ||
        c.contains("maps.apple.com", ignoreCase = true)

    private fun isCalendar(c: String) =
        c.contains("BEGIN:VEVENT", ignoreCase = true)

    private fun isBarcode(c: String) =
        Regex("^[0-9]{8,13}$").matches(c.trim())

    private fun extractDomain(url: String): String? = runCatching {
        val withScheme = if (!url.startsWith("http")) "https://$url" else url
        java.net.URI(withScheme).host?.removePrefix("www.")
    }.getOrNull()

    private fun extractWifiSsid(wifi: String): String? =
        Regex("S:([^;]+)").find(wifi)?.groupValues?.getOrNull(1)

    private fun extractEmail(email: String): String? = when {
        email.startsWith("mailto:", ignoreCase = true) -> email.removePrefix("mailto:").split("?").first()
        Patterns.EMAIL_ADDRESS.matcher(email).matches() -> email
        else -> null
    }

    private fun extractPhone(phone: String): String? = when {
        phone.startsWith("tel:", ignoreCase = true) -> phone.removePrefix("tel:")
        else -> phone.trim()
    }

    fun normalize(content: String): String = content.trim().lowercase()
}
