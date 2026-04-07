package com.epic_engine.swisskit.core.ui

import android.content.Context
import androidx.annotation.StringRes as AndroidStringRes

sealed class UiText {
    class StringRes(@AndroidStringRes val id: Int, vararg val args: Any) : UiText()
    data class Dynamic(val value: String) : UiText()

    fun asString(context: Context): String = when (this) {
        is StringRes -> context.getString(id, *args)
        is Dynamic -> value
    }
}
