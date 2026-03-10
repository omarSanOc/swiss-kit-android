package com.epic_engine.swisskit.feature.finance.domain.usecase

import com.epic_engine.swisskit.feature.finance.data.export.FinancePdfExporter
import com.epic_engine.swisskit.feature.finance.domain.repository.FinanceRepository
import javax.inject.Inject

class ExportFinanceToPdfUseCase @Inject constructor(
    private val repository: FinanceRepository,
    private val pdfExporter: FinancePdfExporter
) {
    suspend operator fun invoke(): Result<ByteArray> {
        return repository.getAll().mapCatching { items ->
            pdfExporter.export(items)
        }
    }
}
