package com.epic_engine.swisskit.feature.shopping.presentation.utils

import android.content.Context
import com.epic_engine.swisskit.R

object ShoppingShareFormatter {

    fun buildShareText(state: ShoppingUiState, context: Context): String {
        val lines = mutableListOf<String>()
        lines.add(context.getString(R.string.shopping_share_list_header))
        lines.add("")
        if (state.pendingItems.isNotEmpty()) {
            lines.add(context.getString(R.string.shopping_share_pending_section))
            state.pendingItems.forEach { lines.add("• ${it.name}") }
        }
        if (state.checkedItems.isNotEmpty()) {
            if (state.pendingItems.isNotEmpty()) lines.add("")
            lines.add(context.getString(R.string.shopping_share_completed_section))
            state.checkedItems.forEach { lines.add("✓ ${it.name}") }
        }
        return lines.joinToString("\n")
    }
}
